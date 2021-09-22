package com.github.jaitl.dynamodb.mapper.converter.collection

import com.github.jaitl.dynamodb.mapper.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SetConverterTest {
    val mapper = Mapper()

    @Test
    fun testWriteEmptySet() {
        data class Data(val set: Set<Instant>)

        val data = Data(emptySet())

        val map = mapper.writeObject(data)

        val set = setAttribute(emptyList())
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadEmptySet() {
        data class Data(val set: Set<Instant>)

        val expectedData = Data(emptySet())

        val set = setAttribute(emptyList())
        val obj = mapOf("set" to set)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteStringSet() {
        data class Data(val set: Set<String>)

        val data = Data(setOf("1", "2", "3"))

        val map = mapper.writeObject(data)

        val set = stringSetAttribute("1", "2", "3")
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadStringSet() {
        data class Data(val set: Set<String>)

        val expectedData = Data(setOf("1", "2", "3"))

        val set = stringSetAttribute("1", "2", "3")
        val obj = mapOf("set" to set)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteNumberSet() {
        data class Data(val set: Set<Int>)

        val data = Data(setOf(1, 2, 3))

        val map = mapper.writeObject(data)

        val set = numberSetAttribute("1", "2", "3")
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadNumberSet() {
        data class Data(val set: Set<Int>)

        val expectedData = Data(setOf(1, 2, 3))

        val set = numberSetAttribute("1", "2", "3")
        val obj = mapOf("set" to set)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteObjectSet() {
        data class SimpleData(val vvv: Int)
        data class Data(val set: Set<SimpleData>)

        val data = Data(setOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val map = mapper.writeObject(data)

        val set = setAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val expectedMap = mapOf("set" to set)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadObjectSet() {
        data class SimpleData(val vvv: Int)
        data class Data(val set: Set<SimpleData>)

        val expectedData = Data(setOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val set = setAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val obj = mapOf("set" to set)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }
}
