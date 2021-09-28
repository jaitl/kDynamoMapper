package pro.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Unions Reader and Mapper implementations into one class.
 *
 * Reads a DynamoDb attribute map to a case class.
 * Writes a case class to a DynamoDb attribute map.
 *
 * @param registry converters for collections and external types.
 */
class Mapper(registry: ConverterRegistry = DEFAULT_REGISTRY) :
    KDynamoMapper {

    private val reader: KDynamoMapperReader = Reader(registry)
    private val writer: KDynamoMapperWriter = Writer(registry)

    /**
     * Reads a DynamoDb attribute map to a case class.
     *
     * @param obj an instance of a DynamoDb attribute map.
     * @param clazz a class reference for the returned value. You can get it by ::class.
     *              For example String::class.
     *
     * @return mapped instance of a data class.
     */
    override fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T =
        reader.readObject(obj, clazz)

    /**
     * Reads AttributeValue to any class.
     * It is an internal method used for recursive reading of nested case classes and collections.
     * You can use it when you have KType for the returned value.
     *
     * @param attr instance of a DynamoDb attribute.
     * @param kType type information about the returned value.
     *              You can get it from the experimental typeof<> function. For example typeof<String>.
     * @return mapped instance of value with type from kType.
     */
    override fun readValue(attr: AttributeValue, kType: KType): Any = reader.readValue(attr, kType)

    /**
     * Writes any object of a case class to a DynamoDb attribute map.
     *
     * @param obj instance of a case class.
     * @return mapped instance of a DynamoDb attribute map.
     */
    override fun writeObject(obj: Any): Map<String, AttributeValue> = writer.writeObject(obj)

    /**
     * Writes any value to a DynamoDb attribute.
     * It is an internal method used for recursive writing of nested case classes and collections.
     * You can use it when you have KType for the value.
     *
     * @param value instance of any value. For example Int, String, Instant, etc.
     * @param kType type information about the value. You can get it
     *              from the experimental typeof<> function.
     *              For example typeof<String>.
     * @return mapped instance of a DynamoDb attribute.
     */
    override fun writeValue(value: Any, kType: KType): AttributeValue =
        writer.writeValue(value, kType)
}
