package pro.jaitl.dynamodb.mapper.converter.collection

import pro.jaitl.dynamodb.mapper.KDynamoMapperReader
import pro.jaitl.dynamodb.mapper.KDynamoMapperWriter
import pro.jaitl.dynamodb.mapper.attribute.listAttribute
import pro.jaitl.dynamodb.mapper.converter.TypeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Converts List<*> to AttributeValue and vice versa.
 */
class ListConverter : TypeConverter<List<*>> {
    /**
     * Reads DynamoDb attribute map to List<*>
     */
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): List<*> {
        val listType = kType.arguments.first().type!!
        return attr.l()
            .filterNotNull()
            .map { reader.readValue(it, listType) }
    }

    /**
     * Writes List<*> to DynamoDb attribute map
     */
    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val collection = value as List<*>
        val listType = kType.arguments.first().type!!
        val list = collection
            .filterNotNull()
            .map { writer.writeValue(it, listType) }

        return listAttribute(list)
    }

    /**
     * @return type of this converter
     */
    override fun type(): KClass<List<*>> = List::class
}
