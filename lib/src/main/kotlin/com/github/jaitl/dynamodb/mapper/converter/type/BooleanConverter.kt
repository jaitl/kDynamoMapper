package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.attribute.booleanAttribute
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class BooleanConverter : TypeConverter<Boolean> {
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Boolean =
        attr.bool()

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        booleanAttribute(value as Boolean)

    override fun type(): KClass<Boolean> = Boolean::class
}
