package pro.jaitl.dynamodb.mapper

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
            return handleAdt(obj, clazz)
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
     * ADT are determined by inheritance from a sealed interface/class. Each ADT has to contain
     * the ADT_FIELD_NAME field with the original class name.
     *
     * @throws RequiredFieldNotFoundException when the ADT_FIELD_NAME field isn't found
     *                                        in the DynamoDb attribute map.
     */
    private fun <T : Any> handleAdt(obj: Map<String, AttributeValue>, kClass: KClass<T>): T {
        val realClazz = obj[ADT_FIELD_NAME]?.s()

        if (realClazz == null) {
            throw RequiredFieldNotFoundException(
                "ADT '$kClass' has to contain attribute '${ADT_FIELD_NAME}'",
                setOf(ADT_FIELD_NAME)
            )
        }

        @Suppress("UNCHECKED_CAST")
        val adtClazz = Class.forName(realClazz).kotlin as KClass<T>

        if (!kClass.isSuperclassOf(adtClazz)) {
            throw UnknownTypeException(
                "Class '${kClass.qualifiedName}' isn't a subclass of '${adtClazz}'"
            )
        }

        return readObject(obj, adtClazz)
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
     * Helper function gets field type out of a KParameter and gets
     * field value out of a DynamoDb attribute map.
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
