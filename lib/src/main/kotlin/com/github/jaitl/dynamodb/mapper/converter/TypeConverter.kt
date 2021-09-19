package com.github.jaitl.dynamodb.mapper.converter

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface TypeConverter<T : Any> {
    fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): T
    fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue
    fun type(): KClass<T>
}
