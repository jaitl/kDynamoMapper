package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts UUID to AttributeValue and vice versa.
 */
class UUIDConverter : TypeConverter<UUID> {
    /**
     * Reads DynamoDb attribute map to UUID
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): UUID =
        UUID.fromString(attr.s())

    /**
     * Writes UUID to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        stringAttribute((value as UUID).toString())

    /**
     * @return type of this converter
     */
    override fun type(): KClass<UUID> = UUID::class
}
