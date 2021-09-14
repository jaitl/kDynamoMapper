package com.github.jaitl.dynamodb.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReaderTest {
    @Test
    fun testSimpleDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        val expectedData = SimpleData("qwerty", 123)
        val obj = dWrite(expectedData)

        val data = dRead(obj, SimpleData::class)
        assertEquals(expectedData, data)
    }
}