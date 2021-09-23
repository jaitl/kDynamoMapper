package com.github.jaitl.dynamodb.mapper.converter

import com.github.jaitl.dynamodb.mapper.AttributeNotFoundException
import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.mapAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

object DtoConverter : TypeConverter<Any> {
    val dtoFieldName = "dto_class_name"

    override fun read(reader: KDynamoMapperReader, attr: AttributeValue, kType: KType): Any {
        val attrMap = attr.m()
        val realClazz = attrMap[dtoFieldName]?.s()

        if (realClazz == null) {
            throw AttributeNotFoundException("Attribute '$dtoFieldName' for DTO: '$kType' doesn't found")
        }

        val clazz = Class.forName(realClazz).kotlin

        return reader.readObject(attrMap, clazz)
    }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue =
        mapAttribute(writer.writeObject(value))

    override fun type(): KClass<Any> = throw NotImplementedError()
}
