package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.UnsupportedKeyTypeException
import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts Map<String, *> to AttributeValue and vice versa.
 *
 * DynamoDb AttributeValue supports map with string key maps only.
 */
class MapConverter : TypeConverter<Map<*, *>> {
    /**
     * Reads DynamoDb attribute map to Map<String, *>
     * @throws UnsupportedKeyTypeException when map's key has type other than string type.
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Map<*, *> {
        val keyType = kType.arguments.first().type!!
        val keyClazz = keyType.classifier as KClass<*>

        if (keyClazz != String::class) {
            throw UnsupportedKeyTypeException(
                "Map doesn't support type '${keyClazz}' as key. " +
                        "Only 'String' type supported as key"
            )
        }

        val valueType = kType.arguments.last().type!!

        return attr.m()
            .mapNotNull { it.key!! to reader.readValue(it.value!!, valueType) }
            .toMap()
    }

    /**
     * Writes Map<String, *> to DynamoDb attribute map
     * @throws UnsupportedKeyTypeException when map's key has type other than string type.
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val collection = value as Map<*, *>
        val keyType = kType.arguments.first().type!!
        val keyClazz = keyType.classifier as KClass<*>

        if (keyClazz != String::class) {
            throw UnsupportedKeyTypeException(
                "Map doesn't support type '${keyClazz}' as key. " +
                        "Only 'String' type supported as key"
            )
        }

        val valueType = kType.arguments.last().type!!

        val map = collection.mapKeys { it.key as String }
            .mapValues { writer.writeValue(it.value!!, valueType) }

        return mapAttribute(map)
    }

    /**
     * @return type of this converter
     */
    override fun type(): KClass<Map<*, *>> = Map::class
}
