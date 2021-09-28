package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import kotlin.test.Test
import kotlin.test.assertEquals


class ExtensionKtTest {
    val mapper = Mapper()

    @Test
    fun testWriteProperty() {
        val string = "data"

        val attr = mapper.writeValue(string)

        val expectedAttr = stringAttribute("data")

        assertEquals(expectedAttr, attr)
    }

    @Test
    fun testReadProperty() {
        val attr = stringAttribute("data")
        val data: String = mapper.readValue(attr)

        val expectedData = "data"

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteObject() {
        data class SimpleData(val string: String, val int: Int)

        val data = SimpleData("test", 123)

        val attr = mapper.writeValue(data)

        val expectedAttr = mapAttribute(mapOf(
            "string" to stringAttribute("test"),
            "int" to numberAttribute(123)
        ))

        assertEquals(expectedAttr, attr)
    }

    @Test
    fun testReadObject() {
        data class SimpleData(val string: String, val int: Int)

        val attr = mapAttribute(mapOf(
            "string" to stringAttribute("test"),
            "int" to numberAttribute(123)
        ))

        val data: SimpleData = mapper.readValue(attr)

        val expectedData = SimpleData("test", 123)

        assertEquals(expectedData, data)
    }
}
