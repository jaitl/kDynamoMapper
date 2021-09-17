package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.converter.NumberConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

fun <T : Any> dRead(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
    if (!clazz.isData) {
        throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
    }
    val constructor = clazz.primaryConstructor!!
    val args = constructor.parameters

    val params = args.associateWith { matchType(it, obj) }
    return constructor.callBy(params)
}

internal fun matchType(param: KParameter, obj: Map<String, AttributeValue>): Any? {
    val attr: AttributeValue? = obj[param.name]
    if (attr == null) {
        return null
    }
    return matchAttributeToClass(attr, param.type)
}

internal fun matchAttributeToClass(attr: AttributeValue, kType: KType): Any {
    val clazz = kType.classifier as KClass<*>
    if (clazz.isData) {
        return dRead(attr.m(), clazz)
    }
    if (clazz.isSubclassOf(Number::class)) {
        return NumberConverter.read(attr, kType)
    }
    if (clazz.isSubclassOf(List::class)) {
        return matchList(attr, kType)
    }
    if (clazz.isSubclassOf(Set::class)) {
        return matchSet(attr, kType)
    }
    if (clazz.isSubclassOf(Map::class)) {
        return matchMap(attr, kType)
    }
    return when (clazz) {
        String::class -> attr.s()
        Boolean::class -> attr.bool()
        UUID::class -> UUID.fromString(attr.s())
        Instant::class -> DateTimeFormatter.ISO_INSTANT.parse(attr.s(), Instant::from)
        else -> throw UnknownTypeException("Unknown type: $clazz")
    }
}

internal fun matchList(attr: AttributeValue, kType: KType): Any {
    val listType = kType.arguments.first().type!!
    return attr.l().filterNotNull().map { matchAttributeToClass(it, listType) }
}

internal fun matchSet(attr: AttributeValue, kType: KType): Any {
    val setType = kType.arguments.first().type!!
    val clazz = setType.classifier as KClass<*>

    if (clazz.isSubclassOf(Number::class)) {
        return attr.ns().map { NumberConverter.read(it, setType) }.toSet()
    }

    return when (clazz) {
        String::class -> attr.ss().toSet()
        else -> attr.l().filterNotNull().map { matchAttributeToClass(it, setType) }.toSet()
    }
}

internal fun matchMap(attr: AttributeValue, kType: KType): Any {
    val keyType = kType.arguments.first().type!!
    val keyClazz = keyType.classifier as KClass<*>

    if (keyClazz != String::class) {
        throw UnsupportedKeyTypeException("Map doesn't support type '${keyClazz}' as key. " +
                "Only 'String' type supported as map's key")
    }

    val valueType = kType.arguments.last().type!!

    return attr.m().mapNotNull { it.key!! to matchAttributeToClass(it.value!!, valueType) }.toMap()
}
