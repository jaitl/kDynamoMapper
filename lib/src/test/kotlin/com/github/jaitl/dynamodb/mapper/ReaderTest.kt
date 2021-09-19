package com.github.jaitl.dynamodb.mapper

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReaderTest {

    val reader: KDynamoMapperReader = Reader()

    @Test(expected = NotDataClassTypeException::class)
    fun testIsntDataType() {
        class SomeClass(val data: String)

        reader.read(emptyMap(), SomeClass::class)
    }

    @Test(expected = UnknownTypeException::class)
    fun testUnknownType() {
        class SomeClass
        data class SimpleData(val some: SomeClass)

        val obj = mapOf(
            "some" to stringAttribute("ddd")
        )

        reader.read(obj, SimpleData::class)
    }

    @Test
    fun testNullField() {
        data class SimpleData(val str: String, val digit: Int?)

        val expectedData = SimpleData("ddd", null)
        val obj = mapOf(
            "str" to stringAttribute("ddd")
        )

        val data = reader.read(obj, SimpleData::class)

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

        val data = reader.read(obj, SimpleData::class)
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

        val nested = reader.read(obj, NestedData::class)

        assertEquals(nestedExpected, nested)
    }

    @Test
    fun testCommonTypes() {
        data class TypeData(val id: UUID, val bool: Boolean, val inst: Instant)

        val dataExpected = TypeData(UUID.randomUUID(), true, Instant.now())

        val obj = mapOf(
            "id" to stringAttribute(dataExpected.id.toString()),
            "bool" to booleanAttribute(dataExpected.bool),
            "inst" to stringAttribute(DateTimeFormatter.ISO_INSTANT.format(dataExpected.inst))
        )

        val data = reader.read(obj, TypeData::class)

        assertEquals(dataExpected, data)
    }

    @Test
    fun testStringList() {
        data class Data(val list: List<String>)

        val expectedData = Data(listOf("1", "2", "3"))

        val list = listAttribute(
            stringAttribute("1"),
            stringAttribute("2"),
            stringAttribute("3"),
        )
        val obj = mapOf("list" to list)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testDataClassList() {
        data class SimpleData(val vvv: Int)
        data class Data(val list: List<SimpleData>)

        val expectedData = Data(listOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val list = listAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val obj = mapOf("list" to list)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testStringSet() {
        data class Data(val set: Set<String>)

        val expectedData = Data(setOf("1", "2", "3"))

        val set = stringSetAttribute("1", "2", "3")
        val obj = mapOf("set" to set)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testNumberSet() {
        data class Data(val set: Set<Int>)

        val expectedData = Data(setOf(1, 2, 3))

        val set = numberSetAttribute("1", "2", "3")
        val obj = mapOf("set" to set)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testObjectSet() {
        data class SimpleData(val vvv: Int)
        data class Data(val set: Set<SimpleData>)

        val expectedData = Data(setOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val set = setAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val obj = mapOf("set" to set)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test(expected = UnsupportedKeyTypeException::class)
    fun testMapUnsupportedKey() {
        data class Data(val map: Map<Int, Int>)

        val dataMap = mapAttribute(mapOf())
        val obj = mapOf("map" to dataMap)

        reader.read(obj, Data::class)
    }

    @Test
    fun testObjectMap() {
        data class SimpleData(val num: Int)
        data class Data(val map: Map<String, SimpleData>)

        val expectedData =
            Data(mapOf("a" to SimpleData(1), "b" to SimpleData(2), "c" to SimpleData(3)))

        val dataMap = mapAttribute(
            mapOf(
                "a" to mapAttribute(mapOf("num" to numberAttribute(1))),
                "b" to mapAttribute(mapOf("num" to numberAttribute(2))),
                "c" to mapAttribute(mapOf("num" to numberAttribute(3)))
            )
        )
        val obj = mapOf("map" to dataMap)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testStringMap() {
        data class Data(val map: Map<String, String>)

        val expectedData = Data(mapOf("a" to "a", "b" to "b", "c" to "c"))

        val dataMap = mapAttribute(
            mapOf(
                "a" to stringAttribute("a"),
                "b" to stringAttribute("b"),
                "c" to stringAttribute("c")
            )
        )
        val obj = mapOf("map" to dataMap)

        val data = reader.read(obj, Data::class)

        assertEquals(expectedData, data)
    }
}
