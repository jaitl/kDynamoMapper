package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter.ISO_INSTANT
import kotlin.reflect.KClass
import kotlin.reflect.KType

class InstantConverter : TypeConverter<Instant> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): Instant =
        ISO_INSTANT.parse(attr.s(), Instant::from)

    override fun write(mapper: KDynamoMapper, value: Any, kType: KType): AttributeValue =
        stringAttribute(ISO_INSTANT.format(value as Instant))

    override fun type(): KClass<Instant> = Instant::class
}
