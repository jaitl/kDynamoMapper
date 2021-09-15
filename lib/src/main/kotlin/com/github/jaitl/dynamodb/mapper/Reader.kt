package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
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
    val clazz = param.type.classifier as KClass<*>
    val attr = obj[param.name]
    if (attr == null) {
        return null
    }
    if (clazz.isData) {
        return dRead(attr.m(), clazz)
    }
    if (clazz.isSubclassOf(Number::class)) {
        return parseNumber(attr.n(), clazz)
    }
    return when(clazz) {
        String::class -> attr.s()
        else -> throw UnknownTypeException(param.type.classifier)
    }
}

internal fun parseNumber(str: String, clazz: KClass<*>): Number {
    return when(clazz) {
        Byte::class -> str.toByte()
        Short::class -> str.toShort()
        Int::class -> str.toInt()
        Int::class -> str.toInt()
        Long::class -> str.toLong()
        Double::class -> str.toDouble()
        Float::class -> str.toFloat()
        else -> throw UnknownTypeException(clazz)
    }
}
