package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor

class Reader(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapperReader {
    override fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
        if (clazz.isSealed) {
            return handleDto(obj, clazz)
        }
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
        }
        val constructor = clazz.primaryConstructor!!
        val args = constructor.parameters

        checkRequiredFields(obj, args, clazz)

        val params = args.associateWith { readParameter(it, obj) }

        return constructor.callBy(params)
    }

    private fun <T : Any> handleDto(obj: Map<String, AttributeValue>, kClass: KClass<T>): T {
        val realClazz = obj[DTO_FIELD_NAME]?.s()

        if (realClazz == null) {
            throw AttributeNotFoundException(
                "DTO '$kClass' has to contain attribute '${DTO_FIELD_NAME}'"
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

    private fun readParameter(param: KParameter, obj: Map<String, AttributeValue>): Any? {
        val attr: AttributeValue? = obj[param.name]
        if (attr == null) {
            return null
        }
        return readValue(attr, param.type)
    }

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
}
