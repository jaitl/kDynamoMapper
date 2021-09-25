package com.github.jaitl.dynamodb.mapper.converter.type

import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import org.junit.Test
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

internal class InstantConverterTest {
    val mapper = Mapper()

    data class InstantData(val instant: Instant)

    @Test
    fun testWriteValue() {
        val data = InstantData(Instant.now())

        val attrMap = mapper.writeObject(data)

        val instantStr = DateTimeFormatter.ISO_INSTANT.format(data.instant)
        val expectedMap = mapOf("instant" to stringAttribute(instantStr))

        assertEquals(expectedMap, attrMap)
    }

    @Test
    fun testReadValue() {
        val expectedData = InstantData(Instant.now())

        val instantStr = DateTimeFormatter.ISO_INSTANT.format(expectedData.instant)

        val attrMap = mapOf("instant" to stringAttribute(instantStr))

        val data = mapper.readObject(attrMap, InstantData::class)

        assertEquals(expectedData, data)
    }
}