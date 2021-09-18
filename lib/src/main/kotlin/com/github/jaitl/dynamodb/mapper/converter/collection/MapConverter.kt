package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.UnsupportedKeyTypeException
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.mapAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class MapConverter : TypeConverter<Map<*, *>> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): Map<*, *> {
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
            .mapNotNull { it.key!! to mapper.readValue(it.value!!, valueType) }
            .toMap()
    }

    override fun write(mapper: KDynamoMapper, value: Any, kType: KType): AttributeValue {
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
            .mapValues { mapper.writeValue(it.value!!, valueType) }

        return mapAttribute(map)
    }

    override fun type(): KClass<Map<*, *>> = Map::class
}
