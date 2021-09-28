package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.mapper.KDynamoMapperReader
import com.github.jaitl.dynamodb.mapper.KDynamoMapperWriter
import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.readValue
import com.github.jaitl.dynamodb.mapper.writeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.KType

class SimpleDataType(val instant: Instant) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleDataType

        if (instant != other.instant) return false

        return true
    }

    override fun hashCode(): Int {
        return instant.hashCode()
    }
}

class ComplexDataType(val string: String, val int: Int, val simpleDataType: SimpleDataType) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComplexDataType

        if (string != other.string) return false
        if (int != other.int) return false
        if (simpleDataType != other.simpleDataType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = string.hashCode()
        result = 31 * result + int
        result = 31 * result + simpleDataType.hashCode()
        return result
    }
}

data class MyDataClass(
    val id: String,
    val simpleDataType: SimpleDataType,
    val complexDataType: ComplexDataType
)

data class MyDataKey(val id: String)

class SimpleDataTypeConverter : TypeConverter<SimpleDataType> {
    override fun read(
        reader: KDynamoMapperReader,
        attr: AttributeValue,
        kType: KType
    ): SimpleDataType {
        return SimpleDataType(
            instant = reader.readValue(attr)
        )
    }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val myData = value as SimpleDataType
        return writer.writeValue(myData.instant)
    }

    override fun type(): KClass<SimpleDataType> = SimpleDataType::class
}

class ComplexDataTypeConverter : TypeConverter<ComplexDataType> {
    override fun read(
        reader: KDynamoMapperReader,
        attr: AttributeValue,
        kType: KType
    ): ComplexDataType {
        val attrMap = attr.m()
        return ComplexDataType(
            string = reader.readValue(attrMap["string"]!!),
            int = reader.readValue(attrMap["int"]!!),
            simpleDataType = reader.readValue(attrMap["simpleDataType"]!!)
        )
    }

    override fun write(writer: KDynamoMapperWriter, value: Any, kType: KType): AttributeValue {
        val myData = value as ComplexDataType
        return mapAttribute(
            mapOf(
                "string" to writer.writeValue(myData.string),
                "int" to writer.writeValue(myData.int),
                "simpleDataType" to writer.writeValue(myData.simpleDataType),
            )
        )
    }

    override fun type(): KClass<ComplexDataType> = ComplexDataType::class
}
