package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

class Writer(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapperWriter {
    override fun writeObject(obj: Any): Map<String, AttributeValue> {
        val clazz = obj::class
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
        }
        val members = clazz.memberProperties
        return members
            .map { it to writeProperty(it, obj) }
            .filter { it.second != null }
            .associate { it.first.name to it.second!! }
    }

    private fun writeProperty(prop: KProperty1<out Any, *>, obj: Any): AttributeValue? {
        val value: Any? = prop.getter.call(obj)
        if (value == null) {
            return null
        }
        return writeValue(value, prop.returnType)
    }

    override fun writeValue(value: Any, kType: KType): AttributeValue {
        val clazz = kType.classifier as KClass<*>
        if (clazz.isData) {
            return mapAttribute(writeObject(value))
        }
        val converter = registry.registry[clazz]
        if (converter != null) {
            return converter.write(this, value, kType)
        }
        val superClasses = clazz.supertypes.mapNotNull { registry.registry[it.classifier] }
        if (superClasses.isNotEmpty()) {
            return superClasses.first().write(this, value, kType)
        }
        throw UnknownTypeException("Unknown type: $clazz")
    }
}
