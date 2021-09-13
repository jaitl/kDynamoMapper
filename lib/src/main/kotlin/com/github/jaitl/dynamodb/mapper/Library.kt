package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun toDynamoDb(obj: Any) : Map<String, AttributeValue> {
    val members = obj::class.memberProperties
    return members.associate { it.name to matchValue(it, obj) }
}

fun matchValue(prop: KProperty1<out Any, *>, obj: Any) : AttributeValue {
    return when(prop.returnType.classifier) {
        String::class -> AttributeValue.builder().s(prop.getter.call(obj) as String).build()
        Int::class -> AttributeValue.builder().n(prop.getter.call(obj).toString()).build()
        else -> throw Exception("err")
    }
}
