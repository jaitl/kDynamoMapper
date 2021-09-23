package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.converter.DtoConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class Reader(private val registry: ConverterRegistry = DEFAULT_REGISTRY) : KDynamoMapperReader {
    override fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T {
        if (!clazz.isData) {
            throw NotDataClassTypeException("Type '${clazz}' isn't data class type")
        }
        val constructor = clazz.primaryConstructor!!
        val args = constructor.parameters

        checkRequiredFields(obj, args, clazz)

        val params = args.associateWith { readParameter(it, obj) }

        return constructor.callBy(params)
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
            return readObject(attr.m(), clazz)
        }
        if (clazz.isSealed) {
            return DtoConverter.read(this, attr, kType)
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

    private fun checkRequiredFields(obj: Map<String, AttributeValue>, args: List<KParameter>, clazz: KClass<*>) {
        val foundFields = obj.keys
        val requiredFields = args.filterNot { it.type.isMarkedNullable }.mapNotNull { it.name }.toSet()

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
