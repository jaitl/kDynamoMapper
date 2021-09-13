package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
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
    if ((prop.returnType.classifier as KClass<*>).isData) {
        return AttributeValue.builder().m(dWrite(prop.getter.call(obj) as Any)).build()
    }
    return when (prop.returnType.classifier) {
        String::class -> AttributeValue.builder().s(prop.getter.call(obj) as String).build()
        Int::class -> AttributeValue.builder().n(prop.getter.call(obj).toString()).build()
        else -> throw UnknownTypeException(prop.returnType.classifier)
    }
}
