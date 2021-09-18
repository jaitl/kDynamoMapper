package com.github.jaitl.dynamodb.mapper.converter

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface TypeConverter<T : Any> {
    fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): T
    fun write(mapper: KDynamoMapper, value: Any, kType: KType): AttributeValue
    fun type(): KClass<T>
}
