package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


internal class UUIDConverterTest {
    val mapper = Mapper()

    data class UUIDData(val uuid: UUID)

    @Test
    fun testWriteValue() {
        val data = UUIDData(UUID.randomUUID())

        val attrMap = mapper.writeObject(data)

        val expectedMap = mapOf("uuid" to stringAttribute(data.uuid.toString()))

        assertEquals(expectedMap, attrMap)
    }

    @Test
    fun testReadValue() {
        val expectedData = UUIDData(UUID.randomUUID())

        val attrMap = mapOf("uuid" to stringAttribute(expectedData.uuid.toString()))

        val data = mapper.readObject(attrMap, UUIDData::class)

        assertEquals(expectedData, data)
    }
}
