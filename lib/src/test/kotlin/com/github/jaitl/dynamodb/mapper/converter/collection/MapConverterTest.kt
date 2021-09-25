package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.UnsupportedKeyTypeException
import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MapConverterTest {
    val mapper = Mapper()

    @Test(expected = UnsupportedKeyTypeException::class)
    fun testWriteMapUnsupportedKey() {
        data class Data(val map: Map<Int, Int>)

        val data = Data(mapOf(1 to 1, 2 to 2))

        mapper.writeObject(data)
    }

    @Test(expected = UnsupportedKeyTypeException::class)
    fun testReadMapUnsupportedKey() {
        data class Data(val map: Map<Int, Int>)

        val dataMap = mapAttribute(mapOf())
        val obj = mapOf("map" to dataMap)

        mapper.readObject(obj, Data::class)
    }

    @Test
    fun testWriteEmptyMap() {
        data class Data(val map: Map<String, String>)

        val data = Data(emptyMap())

        val map = mapper.writeObject(data)

        val dataMap = mapAttribute(emptyMap())
        val expectedMap = mapOf("map" to dataMap)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadEmptyMap() {
        data class Data(val map: Map<String, String>)

        val expectedData = Data(emptyMap())

        val dataMap = mapAttribute(emptyMap())
        val obj = mapOf("map" to dataMap)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteObjectMap() {
        data class SimpleData(val num: Int)
        data class Data(val map: Map<String, SimpleData>)

        val data = Data(mapOf("a" to SimpleData(1), "b" to SimpleData(2), "c" to SimpleData(3)))

        val map = mapper.writeObject(data)

        val dataMap = mapAttribute(
            mapOf(
                "a" to mapAttribute(mapOf("num" to numberAttribute(1))),
                "b" to mapAttribute(mapOf("num" to numberAttribute(2))),
                "c" to mapAttribute(mapOf("num" to numberAttribute(3)))
            )
        )
        val expectedMap = mapOf("map" to dataMap)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadObjectMap() {
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

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteStringMap() {
        data class Data(val map: Map<String, String>)

        val data = Data(mapOf("a" to "a", "b" to "b", "c" to "c"))

        val map = mapper.writeObject(data)

        val dataMap = mapAttribute(
            mapOf(
                "a" to stringAttribute("a"),
                "b" to stringAttribute("b"),
                "c" to stringAttribute("c")
            )
        )
        val expectedMap = mapOf("map" to dataMap)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadStringMap() {
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

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }
}
