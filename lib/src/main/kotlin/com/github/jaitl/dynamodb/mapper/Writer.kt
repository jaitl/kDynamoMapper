package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
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
        .map { it to matchAttribute(it, obj) }
        .filter { it.second != null }
        .associate { it.first.name to it.second!! }
}

internal fun matchAttribute(prop: KProperty1<out Any, *>, obj: Any): AttributeValue? {
    val clazz = prop.returnType.classifier as KClass<*>
    val value: Any? = prop.getter.call(obj)
    if (value == null) {
        return null
    }
    if (clazz.isData) {
        return mapAttribute(dWrite(value))
    }
    if (clazz.isSubclassOf(Number::class)) {
        return numberAttribute(value as Number)
    }
    return when (clazz) {
        String::class -> stringAttribute(value as String)
        else -> throw UnknownTypeException(prop.returnType.classifier)
    }
}
