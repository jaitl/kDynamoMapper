package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.KDynamoMapper
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.listAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

class ListConverter : TypeConverter<List<*>> {
    override fun read(mapper: KDynamoMapper, attr: AttributeValue, kType: KType): List<*> {
        val listType = kType.arguments.first().type!!
        return attr.l()
            .filterNotNull()
            .map { mapper.readValue(it, listType) }
    }

    override fun write(mapper: KDynamoMapper,value: Any, kType: KType): AttributeValue {
        val collection = value as List<*>
        val listType = kType.arguments.first().type!!
        val list = collection
            .filterNotNull()
            .map { mapper.writeValue(it, listType) }

        return listAttribute(list)
    }

    override fun type(): KClass<List<*>> = List::class
}
