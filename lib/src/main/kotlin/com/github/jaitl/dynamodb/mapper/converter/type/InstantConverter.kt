package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter.ISO_INSTANT
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts Instant to AttributeValue and vice versa.
 */
class InstantConverter : TypeConverter<Instant> {
    /**
     * Reads DynamoDb attribute map to Instant
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Instant =
        ISO_INSTANT.parse(attr.s(), Instant::from)

    /**
     * Writes Instant to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        stringAttribute(ISO_INSTANT.format(value as Instant))

    /**
     * @return type of this converter
     */
    override fun type(): KClass<Instant> = Instant::class
}
