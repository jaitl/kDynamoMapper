package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class Mapper(registry: ConverterRegistry = DEFAULT_REGISTRY) :
    KDynamoMapper {

    private val reader: KDynamoMapperReader = Reader(registry)
    private val writer: KDynamoMapperWriter = Writer(registry)

    override fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T =
        reader.readObject(obj, clazz)

    override fun readValue(attr: AttributeValue, kType: KType): Any = reader.readValue(attr, kType)

    override fun writeObject(obj: Any): Map<String, AttributeValue> = writer.writeObject(obj)

    override fun writeValue(value: Any, kType: KType): AttributeValue =
        writer.writeValue(value, kType)
}
