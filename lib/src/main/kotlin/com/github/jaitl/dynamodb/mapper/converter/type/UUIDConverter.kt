package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class UUIDConverter : TypeConverter<UUID> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): UUID = UUID.fromString(attr.s())

    override fun write(mapper: KDynamoMapper,value: Any, kType: KType): AttributeValue =
        stringAttribute((value as UUID).toString())

    override fun type(): KClass<UUID> = UUID::class
}
