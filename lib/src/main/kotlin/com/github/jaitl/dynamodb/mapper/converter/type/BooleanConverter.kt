package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.booleanAttribute
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class BooleanConverter : TypeConverter<Boolean> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): Boolean =
        attr.bool()

    override fun write(mapper: KDynamoMapper, value: Any, kType: KType): AttributeValue =
        booleanAttribute(value as Boolean)

    override fun type(): KClass<Boolean> = Boolean::class
}
