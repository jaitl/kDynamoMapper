package com.github.jaitl.dynamodb.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReaderTest {
    @Test(expected = NotDataClassTypeException::class)
    fun testIsntDataType() {
        class SomeClass(val data: String)

        dRead(emptyMap(), SomeClass::class)
    }

    @Test(expected = UnknownTypeException::class)
    fun testUnknownType() {
        class SomeClass
        data class SimpleData(val some: SomeClass)

        val obj = mapOf(
            "some" to stringAttribute("ddd")
        )

        dRead(obj, SimpleData::class)
    }

    @Test
    fun testNullField() {
        data class SimpleData(val str: String, val digit: Int?)

        val expectedData = SimpleData("ddd", null)
        val obj = mapOf(
            "str" to stringAttribute("ddd")
        )

        val data = dRead(obj, SimpleData::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testSimpleDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        val expectedData = SimpleData("qwerty", 123)
        val obj = mapOf(
            "str" to stringAttribute("qwerty"),
            "digit" to numberAttribute(123)
        )

        val data = dRead(obj, SimpleData::class)
        assertEquals(expectedData, data)
    }
}
