package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class KDynamoMapperImpl(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapper {
    override fun write(obj: Any): Map<String, AttributeValue> {
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

    override fun <T : Any> read(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
        }
        val constructor = clazz.primaryConstructor!!
        val args = constructor.parameters

        val params = args.associateWith { readParameter(it, obj) }
        return constructor.callBy(params)
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
            return mapAttribute(write(value))
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

    private fun readParameter(param: KParameter, obj: Map<String, AttributeValue>): Any? {
        val attr: AttributeValue? = obj[param.name]
        if (attr == null) {
            return null
        }
        return readValue(attr, param.type)
    }

    override fun readValue(attr: AttributeValue, kType: KType): Any {
        val clazz = kType.classifier as KClass<*>
        if (clazz.isData) {
            return read(attr.m(), clazz)
        }
        val converter = registry.registry[clazz]
        if (converter != null) {
            return converter.read(this, attr, kType)
        }
        val superClasses = clazz.supertypes.mapNotNull { registry.registry[it.classifier] }
        if (superClasses.isNotEmpty()) {
            return superClasses.first().read(this, attr, kType)
        }
        throw UnknownTypeException("Unknown type: $clazz")
    }
}
