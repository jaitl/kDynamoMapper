package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.converter.type.NumberConverter
import com.github.jaitl.dynamodb.mapper.numberSetAttribute
import com.github.jaitl.dynamodb.mapper.setAttribute
import com.github.jaitl.dynamodb.mapper.stringSetAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class SetConverter(private val numberConverter: NumberConverter) : TypeConverter<Set<*>> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): Set<*> {
        val setType = kType.arguments.first().type!!
        val clazz = setType.classifier as KClass<*>

        if (clazz.isSubclassOf(Number::class)) {
            return attr.ns().map { numberConverter.read(it, setType) }.toSet()
        }
        if (clazz == String::class) {
            return attr.ss().toSet()
        }

        return attr.l().filterNotNull().map { mapper.readValue(it, setType) }.toSet()
    }

    override fun write(mapper: KDynamoMapper, value: Any, kType: KType): AttributeValue {
        val collection = value as Set<*>
        val setType = kType.arguments.first().type!!
        val clazz = setType.classifier as KClass<*>

        if (clazz.isSubclassOf(Number::class)) {
            val set = collection.filterNotNull().map { it.toString() }.toSet()
            return numberSetAttribute(set)
        }
        if (clazz == String::class) {
            return stringSetAttribute(value as Set<String>)
        }

        val set = collection
            .filterNotNull()
            .map { mapper.writeValue(it, setType) }
            .toList()
        return setAttribute(set)
    }

    override fun type(): KClass<Set<*>> = Set::class
}
