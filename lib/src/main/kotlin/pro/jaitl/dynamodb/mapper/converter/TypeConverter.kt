package pro.jaitl.dynamodb.mapper.converter

import pro.jaitl.dynamodb.mapper.KDynamoMapperReader
import pro.jaitl.dynamodb.mapper.KDynamoMapperWriter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Interface provides a converting functionality. Implementation will convert class to
 * a DynamoDb attribute map and vice versa.
 */
interface TypeConverter<T : Any> {
    /**
     * Reads DynamoDb attribute map to T
     */
    fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): T

    /**
     * Writes T to DynamoDb attribute map
     */
    fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue

    /**
     * @return type of this converter
     */
    fun type(): KClass<T>
}
