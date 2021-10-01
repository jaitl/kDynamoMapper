package pro.jaitl.dynamodb.mapper.converter.type

import pro.jaitl.dynamodb.mapper.KDynamoMapperReader
import pro.jaitl.dynamodb.mapper.KDynamoMapperWriter
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import pro.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts String to AttributeValue and vice versa.
 */
class StringConverter : TypeConverter<String> {
    /**
     * Reads DynamoDb attribute map to String
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): String = attr.s()

    /**
     * Writes String to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        stringAttribute(value as String)

    /**
     * @return type of this converter
     */
    override fun type(): KClass<String> = String::class
}
