package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

fun <T: Any>dRead(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
    if(!clazz.isData) {
        throw NotDataClassTypeException(clazz)
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
        return parseNumber(attr.n(), clazz)
    }
    if (clazz.isSubclassOf(List::class)) {
        return matchList(attr, kType)
    }
    if (clazz.isSubclassOf(Set::class)) {
        return matchSet(attr, kType)
    }
    return when(clazz) {
        String::class -> attr.s()
        Boolean::class -> attr.bool()
        UUID::class -> UUID.fromString(attr.s())
        Instant::class -> DateTimeFormatter.ISO_INSTANT.parse(attr.s(), Instant::from)
        else -> throw UnknownTypeException(clazz)
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
        return attr.ns().map { parseNumber(it, clazz) }.toSet()
    }
    
    return when(clazz) {
        String::class -> attr.ss().toSet()
        else -> attr.l().filterNotNull().map { matchAttributeToClass(it, setType) }.toSet()
    }
}

internal fun parseNumber(str: String, clazz: KClass<*>): Number {
    return when(clazz) {
        Byte::class -> str.toByte()
        Short::class -> str.toShort()
        Int::class -> str.toInt()
        Long::class -> str.toLong()
        Float::class -> str.toFloat()
        Double::class -> str.toDouble()
        else -> throw UnknownTypeException(clazz)
    }
}
