package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

fun dWrite(obj: Any): Map<String, AttributeValue> {
    val clazz = obj::class
    if (!clazz.isData) {
        throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
    }
    val members = clazz.memberProperties
    return members
        .map { it to handleProperty(it, obj) }
        .filter { it.second != null }
        .associate { it.first.name to it.second!! }
}

internal fun handleProperty(prop: KProperty1<out Any, *>, obj: Any): AttributeValue? {
    val value: Any? = prop.getter.call(obj)
    if (value == null) {
        return null
    }
    return matchClassToAttribute(value, prop.returnType)
}

internal fun matchClassToAttribute(value: Any, kType: KType): AttributeValue {
    val clazz = kType.classifier as KClass<*>
    if (clazz.isData) {
        return mapAttribute(dWrite(value))
    }
    if (clazz.isSubclassOf(Number::class)) {
        return numberAttribute(value as Number)
    }
    if (clazz.isSubclassOf(List::class)) {
        return handleList(value as List<*>, kType)
    }
    if (clazz.isSubclassOf(Set::class)) {
        return handleSet(value as Set<*>, kType)
    }
    if (clazz.isSubclassOf(Map::class)) {
        return handleMap(value as Map<*, *>, kType)
    }
    return when (clazz) {
        String::class -> stringAttribute(value as String)
        Boolean::class -> booleanAttribute(value as Boolean)
        Instant::class -> instantAttribute(value as Instant)
        UUID::class -> uuidAttribute(value as UUID)
        else -> throw UnknownTypeException("Unknown type: $clazz")
    }
}

internal fun handleList(value: List<*>, kType: KType): AttributeValue {
    val listType = kType.arguments.first().type!!
    val list = value
        .filterNotNull()
        .map { matchClassToAttribute(it, listType) }
    return listAttribute(list)
}

internal fun handleSet(value: Set<*>, kType: KType): AttributeValue {
    val setType = kType.arguments.first().type!!
    val clazz = setType.classifier as KClass<*>

    if (clazz.isSubclassOf(Number::class)) {
        val set = value.filterNotNull().map { it.toString() }.toSet()
        return numberSetAttribute(set)
    }

    return when(clazz) {
        String::class -> stringSetAttribute(value as Set<String>)
        else -> {
            val set = value
                .filterNotNull()
                .map { matchClassToAttribute(it, setType) }
                .toList()
            return setAttribute(set)
        }
    }
}

internal fun handleMap(map: Map<*, *>, kType: KType): AttributeValue {
    val keyType = kType.arguments.first().type!!
    val keyClazz = keyType.classifier as KClass<*>

    if (keyClazz != String::class) {
        throw UnsupportedKeyTypeException("Map doesn't support type '${keyClazz}' as key. " +
                "Only 'String' type supported as map's key")
    }

    val valueType = kType.arguments.last().type!!

    val value = map.mapKeys { it.key as String }
        .mapValues { matchClassToAttribute(it.value!!, valueType) }

    return mapAttribute(value)
}
