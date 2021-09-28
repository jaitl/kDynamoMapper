package pro.jaitl.dynamodb.mapper.converter.type

import pro.jaitl.dynamodb.mapper.KDynamoMapperReader
import pro.jaitl.dynamodb.mapper.KDynamoMapperWriter
import pro.jaitl.dynamodb.mapper.attribute.booleanAttribute
import pro.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts Boolean to AttributeValue and vice versa.
 */
class BooleanConverter : TypeConverter<Boolean> {
    /**
     * Reads DynamoDb attribute map to Boolean
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Boolean =
        attr.bool()

    /**
     * Writes Boolean to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        booleanAttribute(value as Boolean)

    /**
     * @return type of this converter
     */
    override fun type(): KClass<Boolean> = Boolean::class
}
