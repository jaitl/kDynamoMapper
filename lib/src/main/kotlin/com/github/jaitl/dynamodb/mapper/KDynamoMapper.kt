package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface KDynamoMapper {
    fun write(obj: Any): Map<String, AttributeValue>
    fun writeValue(value: Any, kType: KType) : AttributeValue
    fun <T : Any> read(obj: Map<String, AttributeValue>, clazz: KClass<T>): T
    fun readValue(attr: AttributeValue, kType: KType): Any
}
