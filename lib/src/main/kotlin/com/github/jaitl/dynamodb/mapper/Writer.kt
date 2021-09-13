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
    return members.associate { it.name to matchAttribute(it, obj) }
}

internal fun matchAttribute(prop: KProperty1<out Any, *>, obj: Any): AttributeValue {
    val clazz = prop.returnType.classifier as KClass<*>
    if (clazz.isData) {
        return mapAttribute(dWrite(prop.getter.call(obj) as Any))
    }
    if (clazz.isSubclassOf(Number::class)) {
        return numberAttribute(prop.getter.call(obj) as Number)
    }
    return when (prop.returnType.classifier) {
        String::class -> stringAttribute(prop.getter.call(obj) as String)
        else -> throw UnknownTypeException(prop.returnType.classifier)
    }
}
