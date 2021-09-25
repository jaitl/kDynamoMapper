package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.*
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class NumberConverter : TypeConverter<Number> {
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Number =
        read(attr.n(), kType)

    fun read(str: String, kType: KType): Number =
        when (kType.classifier) {
            Byte::class -> str.toByte()
            Short::class -> str.toShort()
            Int::class -> str.toInt()
            Long::class -> str.toLong()
            Float::class -> str.toFloat()
            Double::class -> str.toDouble()
            else -> throw UnknownTypeException("Unknown type: ${kType.classifier}")
        }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        numberAttribute(value as Number)

    override fun type(): KClass<Number> = Number::class
}
