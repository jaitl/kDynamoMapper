package com.github.jaitl.dynamodb.mapper

import java.time.Instant
import java.util.*
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

    @Test
    fun testNestedSimpleDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        data class NestedData(val sometd: String, val data: SimpleData)

        val nestedExpected = NestedData("abc", SimpleData("ddd", 123))
        val simpleDataMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        val obj = mapOf(
            "sometd" to stringAttribute("abc"),
            "data" to mapAttribute(simpleDataMap)
        )

        val nested = dRead(obj, NestedData::class)

        assertEquals(nestedExpected, nested)
    }

    @Test
    fun testCommonTypes() {
        data class TypeData(val id: UUID, val bool: Boolean, val inst: Instant)

        val dataExpected = TypeData(UUID.randomUUID(), true, Instant.now())

        val obj = mapOf(
            "id" to uuidAttribute(dataExpected.id),
            "bool" to booleanAttribute(dataExpected.bool),
            "inst" to instantAttribute(dataExpected.inst)
        )

        val data = dRead(obj, TypeData::class)

        assertEquals(dataExpected, data)
    }
}
