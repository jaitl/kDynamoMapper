package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
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

internal fun matchType(param: KParameter, obj: Map<String, AttributeValue>): Any {
    val attr = obj[param.name]!!

    return when(param.type.classifier) {
        Int::class -> attr.n().toInt()
        String::class -> attr.s()
        else -> throw UnknownTypeException(param.type.classifier)
    }
}
