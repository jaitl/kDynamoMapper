package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.listAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class ListConverter : TypeConverter<List<*>> {
    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): List<*> {
        val listType = kType.arguments.first().type!!
        return attr.l()
            .filterNotNull()
            .map { reader.readValue(it, listType) }
    }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val collection = value as List<*>
        val listType = kType.arguments.first().type!!
        val list = collection
            .filterNotNull()
            .map { writer.writeValue(it, listType) }

        return listAttribute(list)
    }

    override fun type(): KClass<List<*>> = List::class
}
