package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.test.Test
import kotlin.test.assertEquals

data class MyTest(val str: String, val digit: Int)

class LibraryTest {
    @Test
    fun testSomeLibraryMethod() {
        val tt = MyTest("ddd", 123)
        val map = toDynamoDb(tt)
        val expectedMap = mapOf(
            "str" to AttributeValue.builder().s("ddd").build(),
            "digit" to AttributeValue.builder().n("123").build()
        )
        assertEquals(expectedMap, map)
    }
}
