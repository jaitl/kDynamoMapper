package com.github.jaitl.dynamodb.mapper

import java.time.Instant
import java.util.*
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

    @Test
    fun testCommonTypes() {
        data class TypeData(val id: UUID, val bool: Boolean, val inst: Instant)

        val data = TypeData(UUID.randomUUID(), true, Instant.now())

        val map = dWrite(data)

        val expectedMap = mapOf(
            "id" to uuidAttribute(data.id),
            "bool" to booleanAttribute(data.bool),
            "inst" to instantAttribute(data.inst)
        )

        assertEquals(expectedMap, map)
    }

    @Test
    fun testStringList() {
        data class Data(val list: List<String>)

        val data = Data(listOf("1", "2", "3"))

        val map = dWrite(data)

        val list = listAttribute(
            stringAttribute("1"),
            stringAttribute("2"),
            stringAttribute("3"),
        )
        val expectedMap = mapOf("list" to list)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testDataClassList() {
        data class SimpleData(val vvv: Int)
        data class Data(val list: List<SimpleData>)

        val data = Data(listOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val map = dWrite(data)

        val list = listAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val expectedMap = mapOf("list" to list)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testStringSet() {
        data class Data(val set: Set<String>)

        val data = Data(setOf("1", "2", "3"))

        val map = dWrite(data)

        val set = stringSetAttribute("1", "2", "3")
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testNumberSet() {
        data class Data(val set: Set<Int>)

        val data = Data(setOf(1, 2, 3))

        val map = dWrite(data)

        val set = numberSetAttribute("1", "2", "3")
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testObjectSet() {
        data class SimpleData(val vvv: Int)
        data class Data(val set: Set<SimpleData>)

        val data = Data(setOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val map = dWrite(data)

        val set = setAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test(expected = UnsupportedKeyTypeException::class)
    fun testMapUnsupportedKey() {
        data class Data(val map: Map<Int, Int>)
        val data = Data(mapOf(1 to 1, 2 to 2))

        dWrite(data)
    }

    @Test
    fun testObjectMap() {
        data class SimpleData(val num: Int)
        data class Data(val map: Map<String, SimpleData>)

        val data = Data(mapOf("a" to SimpleData(1), "b" to SimpleData(2), "c" to SimpleData(3)))

        val map = dWrite(data)

        val dataMap = mapAttribute(mapOf(
            "a" to mapAttribute(mapOf("num" to numberAttribute(1))),
            "b" to mapAttribute(mapOf("num" to numberAttribute(2))),
            "c" to mapAttribute(mapOf("num" to numberAttribute(3)))
        ))
        val expectedMap = mapOf("map" to dataMap)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testStringMap() {
        data class Data(val map: Map<String, String>)

        val data = Data(mapOf("a" to "a", "b" to "b", "c" to "c"))

        val map = dWrite(data)

        val dataMap = mapAttribute(mapOf(
            "a" to stringAttribute("a"),
            "b" to stringAttribute("b"),
            "c" to stringAttribute("c")
        ))
        val expectedMap = mapOf("map" to dataMap)

        assertEquals(expectedMap, map)
    }
}
