package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.helper.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DtoTest {

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

    @Test
    fun testReadDtoBySealedClass() {
        val dtoMap = mapOf(
            "long" to numberAttribute(1234),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.DtoTwo")
        )

        val data = mapper.readObject(dtoMap, SimpleDto::class)

        val expectedData = DtoTwo(1234)

        assertEquals(expectedData, data)
    }

    @Test
    fun testReadDtoBySealedClassInherited() {
        val dtoMap = mapOf(
            "double" to numberAttribute(1234.654),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.DtoSecondOne")
        )

        val data = mapper.readObject(dtoMap, SimpleDto::class)

        val expectedData = DtoSecondOne(1234.654)

        assertEquals(expectedData, data)
    }

    @Test(expected = UnknownTypeException::class)
    fun testReadDtoBySealedClassWrongClass() {
        val dtoMap = mapOf(
            "long" to numberAttribute(1234),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDataDto")
        )

        mapper.readObject(dtoMap, SimpleDto::class)
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
