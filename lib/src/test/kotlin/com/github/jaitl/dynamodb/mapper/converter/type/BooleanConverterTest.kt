package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.booleanAttribute
import org.junit.Test
import kotlin.test.assertEquals

internal class BooleanConverterTest {
    val mapper = Mapper()

    data class BooleanData(val boolean: Boolean)

    @Test
    fun testWriteValue() {
        val data = BooleanData(true)

        val attrMap = mapper.writeObject(data)

        val expectedMap = mapOf("boolean" to booleanAttribute(true))

        assertEquals(expectedMap, attrMap)
    }

    @Test
    fun testReadValue() {
        val expectedData = BooleanData(true)

        val attrMap = mapOf("boolean" to booleanAttribute(true))

        val data = mapper.readObject(attrMap, BooleanData::class)

        assertEquals(expectedData, data)
    }
}
