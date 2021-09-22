package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.stringAttribute
import org.junit.Test
import kotlin.test.assertEquals


internal class StringConverterTest {
    val mapper = Mapper()

    data class StringData(val string: String)

    @Test
    fun testWriteValue() {
        val data = StringData("something")

        val attrMap = mapper.writeObject(data)

        val expectedMap = mapOf("string" to stringAttribute("something"))

        assertEquals(expectedMap, attrMap)
    }

    @Test
    fun testReadValue() {
        val expectedData = StringData("something")

        val attrMap = mapOf("string" to stringAttribute("something"))

        val data = mapper.readObject(attrMap, StringData::class)

        assertEquals(expectedData, data)
    }
}