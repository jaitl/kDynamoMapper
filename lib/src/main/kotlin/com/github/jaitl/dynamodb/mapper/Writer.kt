package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

fun dWrite(obj: Any): Map<String, AttributeValue> {
    val clazz = obj::class
    if (!clazz.isData) {
        throw NotDataClassTypeException(clazz)
    }
    val members = clazz.memberProperties
    return members
        .map { it to handleProperty(it, obj) }
        .filter { it.second != null }
        .associate { it.first.name to it.second!! }
}

internal fun handleProperty(prop: KProperty1<out Any, *>, obj: Any): AttributeValue? {
    val clazz = prop.returnType.classifier as KClass<*>
    val value: Any? = prop.getter.call(obj)
    if (value == null) {
        return null
    }
    return matchClassToAttribute(value, clazz)
}

internal fun matchClassToAttribute(value: Any, clazz: KClass<*>): AttributeValue {
    if (clazz.isData) {
        return mapAttribute(dWrite(value))
    }
    if (clazz.isSubclassOf(Number::class)) {
        return numberAttribute(value as Number)
    }
    if (clazz.isSubclassOf(List::class)) {
        return handleList(value as List<*>)
    }
    return when (clazz) {
        String::class -> stringAttribute(value as String)
        Boolean::class -> booleanAttribute(value as Boolean)
        Instant::class -> instantAttribute(value as Instant)
        UUID::class -> uuidAttribute(value as UUID)
        else -> throw UnknownTypeException(clazz)
    }
}

internal fun handleList(value: List<*>): AttributeValue {
    val list = value
        .filterNotNull()
        .map { matchClassToAttribute(it, it::class) }
    return listAttribute(list)
}
