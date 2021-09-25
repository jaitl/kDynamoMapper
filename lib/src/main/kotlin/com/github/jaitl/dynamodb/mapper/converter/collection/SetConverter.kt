package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.attribute.numberSetAttribute
import com.github.jaitl.dynamodb.mapper.attribute.setAttribute
import com.github.jaitl.dynamodb.mapper.attribute.stringSetAttribute
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.converter.type.NumberConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

/**
 * Converts Set<*> to AttributeValue and vice versa.
 * Supports Number Set and String Set.
 *
 * Set<*> stores as List to DynamoDb AttributeValue
 */
class SetConverter(private val numberConverter: NumberConverter) : TypeConverter<Set<*>> {
    /**
     * Reads DynamoDb attribute map to Set<*>
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Set<*> {
        val setType = kType.arguments.first().type!!
        val clazz = setType.classifier as KClass<*>

        if (clazz.isSubclassOf(Number::class)) {
            return attr.ns().map { numberConverter.read(it, setType) }.toSet()
        }
        if (clazz == String::class) {
            return attr.ss().toSet()
        }

        return attr.l().filterNotNull().map { reader.readValue(it, setType) }.toSet()
    }

    /**
     * Writes Set<*> to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val collection = value as Set<*>
        val setType = kType.arguments.first().type!!
        val clazz = setType.classifier as KClass<*>

        if (clazz.isSubclassOf(Number::class)) {
            val set = collection.map { it.toString() }.toSet()
            return numberSetAttribute(set)
        }
        if (clazz == String::class) {
            return stringSetAttribute(collection.map { it.toString() }.toSet())
        }

        val set = collection
            .filterNotNull()
            .map { writer.writeValue(it, setType) }
            .toList()
        return setAttribute(set)
    }

    /**
     * Type of converter
     */
    override fun type(): KClass<Set<*>> = Set::class
}
