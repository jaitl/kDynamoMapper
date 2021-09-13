package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.test.Test
import kotlin.test.assertEquals

class WriterTest {
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
    fun testSimpleData() {
        data class SimpleData(val str: String, val digit: Int)

        val simple = SimpleData("ddd", 123)
        val map = dWrite(simple)
        val expectedMap = mapOf(
            "str" to AttributeValue.builder().s("ddd").build(),
            "digit" to AttributeValue.builder().n("123").build()
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
            "str" to AttributeValue.builder().s("ddd").build(),
            "digit" to AttributeValue.builder().n("123").build()
        )
        val expectedMap = mapOf(
            "sometd" to AttributeValue.builder().s("abc").build(),
            "data" to AttributeValue.builder().m(simpleDataMap).build()
        )
        assertEquals(expectedMap, map)
    }
}
