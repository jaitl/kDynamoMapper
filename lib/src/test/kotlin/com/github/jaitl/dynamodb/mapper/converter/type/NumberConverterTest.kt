package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.UnknownTypeException
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NumberConverterTest {
    val mapper = Mapper()

    data class NumberData(
        val byte: Byte,
        val short: Short,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double
    )

    @Test
    fun testWriteValue() {
        val data = NumberData(1, 2, 3, 4, 5.0f, 6.0)

        val attrMap = mapper.writeObject(data)

        val expectedMap = mapOf(
            "byte" to numberAttribute(1),
            "short" to numberAttribute(2),
            "int" to numberAttribute(3),
            "long" to numberAttribute(4),
            "float" to numberAttribute(5.0f),
            "double" to numberAttribute(6.0),
        )

        assertEquals(expectedMap, attrMap)
    }

    @Test
    fun testReadValue() {
        val expectedData = NumberData(1, 2, 3, 4, 5.0f, 6.0)

        val attrMap = mapOf(
            "byte" to numberAttribute(1),
            "short" to numberAttribute(2),
            "int" to numberAttribute(3),
            "long" to numberAttribute(4),
            "float" to numberAttribute(5.0f),
            "double" to numberAttribute(6.0),
        )

        val data = mapper.readObject(attrMap, NumberData::class)

        assertEquals(expectedData, data)
    }

    @Test(expected = UnknownTypeException::class)
    fun testReadUnsupportedType() {
        val data = mapOf("number" to numberAttribute(1))
        data class MyData(val number: MyTestNumber)

        mapper.readObject(data, MyData::class)
    }
}
