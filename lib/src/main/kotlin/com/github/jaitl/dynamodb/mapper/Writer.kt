package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

/**
 * Writes a case class to DynamoDb attribute map.
 *
 * @param registry list of converters for collections and external types.
 */
class Writer(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapperWriter {
    /**
     * Writes any object of a case class to DynamoDb attribute map.
     *
     * @param obj instance of a case class.
     * @return mapped instance of a DynamoDb attribute map.
     */
    override fun writeObject(obj: Any): Map<String, AttributeValue> {
        val clazz = obj::class
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
        }
        val members = clazz.memberProperties
        val attrsMap = members
            .map { it to writeProperty(it, obj) }
            .filter { it.second != null }
            .associate { it.first.name to it.second!! }

        return attrsMap + dtoClassInfo(clazz)
    }

    /**
     * DTO determines by inheritance from a sealed interface/class. Each DTO has to contain
     * the DTO_FIELD_NAME field with an original class name.
     */
    private fun dtoClassInfo(clazz: KClass<*>): Map<String, AttributeValue> {
        val hasSealedParent = clazz.supertypes
            .map { it.classifier as KClass<*> }
            .any { it.isSealed }

        if (hasSealedParent) {
            return mapOf(DTO_FIELD_NAME to stringAttribute(clazz.qualifiedName!!))
        }

        return emptyMap()
    }

    /**
     * Helper function gets out field type and field value from the KProperty1.
     */
    private fun writeProperty(prop: KProperty1<out Any, *>, obj: Any): AttributeValue? {
        val value: Any? = prop.getter.call(obj)
        if (value == null) {
            return null
        }
        return writeValue(value, prop.returnType)
    }

    /**
     * Writes any value to AttributeValue.
     * It is an internal method used for recursive writing of nested case classes and collections.
     * You can use it when you have KType for the value.
     *
     * @param value instance of any value. For example Int, String, Instant, etc.
     * @param kType type information about the value. You can get it
     *              from the experimental typeof<> function.
     *              For example typeof<String>.
     * @return mapped instance a DynamoDb attribute.
     */
    override fun writeValue(value: Any, kType: KType): AttributeValue {
        val clazz = kType.classifier as KClass<*>
        if (clazz.isSealed || clazz.isData) {
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
