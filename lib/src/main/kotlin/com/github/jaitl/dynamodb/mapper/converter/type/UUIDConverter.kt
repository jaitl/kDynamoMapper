package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class UUIDConverter : TypeConverter<UUID> {
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): UUID =
        UUID.fromString(attr.s())

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        stringAttribute((value as UUID).toString())

    override fun type(): KClass<UUID> = UUID::class
}
