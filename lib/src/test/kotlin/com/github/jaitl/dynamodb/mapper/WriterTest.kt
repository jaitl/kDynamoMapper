package com.github.jaitl.dynamodb.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriterTest {
    @Test(expected = NotDataClassTypeException::class)
    fun testIsntDataType() {
        class SomeClass(val data: String)

        val simple = SomeClass("ddd")
        dWrite(simple)
    }

    @Test(expected = UnknownTypeException::class)
    fun testUnknownType() {
        class SomeClass
        data class SimpleData(val some: SomeClass)

        val simple = SimpleData(SomeClass())
        dWrite(simple)
    }

    @Test
    fun testNullField() {
        data class SimpleData(val str: String, val digit: Int?)

        val simple = SimpleData("ddd", null)
        val map = dWrite(simple)
        val expectedMap = mapOf(
            "str" to stringAttribute("ddd")
        )
        assertEquals(expectedMap, map)
    }

    @Test
    fun testSimpleData() {
        data class SimpleData(val str: String, val digit: Int)

        val simple = SimpleData("ddd", 123)
        val map = dWrite(simple)
        val expectedMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        assertEquals(expectedMap, map)
    }

    @Test
    fun testNestedDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        data class NestedData(val sometd: String, val data: SimpleData)

        val nested = NestedData("abc", SimpleData("ddd", 123))
        val map = dWrite(nested)

        val simpleDataMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        val expectedMap = mapOf(
            "sometd" to stringAttribute("abc"),
            "data" to mapAttribute(simpleDataMap)
        )
        assertEquals(expectedMap, map)
    }
}
