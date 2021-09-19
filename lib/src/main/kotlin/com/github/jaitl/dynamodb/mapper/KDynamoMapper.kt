package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface KDynamoMapperWriter {
    fun write(obj: Any): Map<String, AttributeValue>
    fun writeValue(value: Any, kType: KType): AttributeValue
}

interface KDynamoMapperReader {
    fun <T : Any> read(obj: Map<String, AttributeValue>, clazz: KClass<T>): T
    fun readValue(attr: AttributeValue, kType: KType): Any
}

interface KDynamoMapper : KDynamoMapperReader, KDynamoMapperWriter
