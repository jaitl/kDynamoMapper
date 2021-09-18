package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class StringConverter : TypeConverter<String> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): String = attr.s()

    override fun write(mapper: KDynamoMapper,value: Any, kType: KType): AttributeValue =
        stringAttribute(value as String)

    override fun type(): KClass<String> = String::class
}
