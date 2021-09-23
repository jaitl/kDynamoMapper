package com.github.jaitl.dynamodb.mapper.converter

import com.github.jaitl.dynamodb.mapper.AttributeNotFoundException
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.helper.DtoOne
import com.github.jaitl.dynamodb.mapper.helper.SimpleDataDto
import com.github.jaitl.dynamodb.mapper.mapAttribute
import com.github.jaitl.dynamodb.mapper.stringAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DtoConverterTest {

    val mapper = Mapper()

    @Test
    fun testWriteSimpleDto() {
        val data = SimpleDataDto(DtoOne("test"))

        val map = mapper.writeObject(data)

        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.DtoOne")
        )
        val expectedMap = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadSimpleDto() {
        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.DtoOne")
        )
        val obj = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        val data = mapper.readObject(obj, SimpleDataDto::class)

        val expectedData = SimpleDataDto(DtoOne("test"))

        assertEquals(expectedData, data)
    }

    @Test(expected = ClassNotFoundException::class)
    fun testReadNotExistsClass() {
        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.Dto")
        )
        val obj = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        mapper.readObject(obj, SimpleDataDto::class)
    }

    @Test(expected = AttributeNotFoundException::class)
    fun testReadNotFoundAttribute() {
        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
        )
        val obj = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        mapper.readObject(obj, SimpleDataDto::class)
    }
}
