package pro.jaitl.dynamodb.mapper.converter.type

import pro.jaitl.dynamodb.mapper.*
import pro.jaitl.dynamodb.mapper.converter.TypeConverter
import pro.jaitl.dynamodb.mapper.attribute.numberAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts Number to AttributeValue and vice versa.
 */
class NumberConverter : TypeConverter<Number> {
    /**
     * Reads DynamoDb attribute map to Number
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Number =
        read(attr.n(), kType)

    /**
     * Writes Number to DynamoDb attribute map
     */
    fun read(str: String, kType: KType): Number =
        when (kType.classifier) {
            Byte::class -> str.toByte()
            Short::class -> str.toShort()
            Int::class -> str.toInt()
            Long::class -> str.toLong()
            Float::class -> str.toFloat()
            Double::class -> str.toDouble()
            else -> throw UnknownTypeException("Unknown type: ${kType.classifier}")
        }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        numberAttribute(value as Number)

    /**
     * @return type of this converter
     */
    override fun type(): KClass<Number> = Number::class
}
