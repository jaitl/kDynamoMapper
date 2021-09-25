package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor

/**
 * Reads a DynamoDb attribute map to a case class.
 *
 * @param registry converters for collections and external types.
 */
class Reader(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapperReader {
    /**
     * Reads a DynamoDb attribute map to a case class.
     *
     * @param obj an instance of a DynamoDb attribute map.
     * @param clazz a class reference for the returned value. You can get it by ::class.
     *              For example String::class.
     *
     * @return mapped instance of a data class.
     */
    override fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
        if (clazz.isSealed) {
            return handleDto(obj, clazz)
        }
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't a data class type")
        }
        val constructor = clazz.primaryConstructor!!
        val args = constructor.parameters

        checkRequiredFields(obj, args, clazz)

        val params = args.associateWith { readParameter(it, obj) }

        return constructor.callBy(params)
    }

    /**
     * DTO determines by inheritance from a sealed interface/class. Each DTO has to contain
     * the DTO_FIELD_NAME field with the original class name.
     *
     * @throws RequiredFieldNotFoundException when the DTO_FIELD_NAME field isn't found
     *                                        in the DynamoDb attribute map.
     */
    private fun <T : Any> handleDto(obj: Map<String, AttributeValue>, kClass: KClass<T>): T {
        val realClazz = obj[DTO_FIELD_NAME]?.s()

        if (realClazz == null) {
            throw RequiredFieldNotFoundException(
                "DTO '$kClass' has to contain attribute '${DTO_FIELD_NAME}'",
                setOf(DTO_FIELD_NAME)
            )
        }

        @Suppress("UNCHECKED_CAST")
        val dtoClazz = Class.forName(realClazz).kotlin as KClass<T>

        if (!kClass.isSuperclassOf(dtoClazz)) {
            throw UnknownTypeException(
                "Class '${kClass.qualifiedName}' isn't subclass of '${dtoClazz}'"
            )
        }

        return readObject(obj, dtoClazz)
    }

    /**
     * Checks that the DynamoDb attribute map contains all required fields for the data class.
     *
     * @throws RequiredFieldNotFoundException when all required for the data class isn't found
     *                                        in the DynamoDb attribute map.
     */
    private fun checkRequiredFields(
        obj: Map<String, AttributeValue>,
        args: List<KParameter>,
        clazz: KClass<*>
    ) {
        val foundFields = obj.keys
        val requiredFields =
            args.filterNot { it.type.isMarkedNullable }.mapNotNull { it.name }.toSet()

        val notFoundFields = requiredFields - foundFields

        if (notFoundFields.isNotEmpty()) {
            throw RequiredFieldNotFoundException(
                "Required fields not found: [${
                    notFoundFields.joinToString(
                        ", ", "'", "'"
                    )
                }] for $clazz", notFoundFields
            )
        }
    }

    /**
     * Helper function gets out field type from the KParameter and gets out
     * field value from a DynamoDb attribute map.
     */
    private fun readParameter(param: KParameter, obj: Map<String, AttributeValue>): Any? {
        val attr: AttributeValue? = obj[param.name]
        if (attr == null) {
            return null
        }
        return readValue(attr, param.type)
    }

    /**
     * Reads AttributeValue to any class.
     * It is an internal method used for recursive reading of nested case classes and collections.
     * You can use it when you have KType for the returned value.
     *
     * @param attr instance of DynamoDb attribute.
     * @param kType type information about the returned value.
     *              You can get it from the experimental typeof<> function. For example typeof<String>.
     * @return mapped instance of value with type from kType.
     */
    override fun readValue(attr: AttributeValue, kType: KType): Any {
        val clazz = kType.classifier as KClass<*>
        if (clazz.isData || clazz.isSealed) {
            return readObject(attr.m(), clazz)
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
