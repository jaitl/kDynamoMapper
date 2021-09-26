package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import com.github.jaitl.dynamodb.mapper.attribute.stringAttribute
import com.github.jaitl.dynamodb.mapper.helper.SimpleDataDto
import com.github.jaitl.dynamodb.mapper.helper.SimpleDto
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DtoTest {

    val mapper = Mapper()

    @Test
    fun testWriteSimpleDto() {
        val data = SimpleDataDto(SimpleDto.DtoOne("test"))

        val map = mapper.writeObject(data)

        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDto\$DtoOne")
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
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDto\$DtoOne")
        )
        val obj = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        val data = mapper.readObject(obj, SimpleDataDto::class)

        val expectedData = SimpleDataDto(SimpleDto.DtoOne("test"))

        assertEquals(expectedData, data)
    }

    @Test
    fun testReadDtoBySealedClass() {
        val dtoMap = mapOf(
            "long" to numberAttribute(1234),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDto\$DtoTwo")
        )

        val data = mapper.readObject(dtoMap, SimpleDto::class)

        val expectedData = SimpleDto.DtoTwo(1234)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteDtoBySealedClassInherited() {
        val data = SimpleDto.SecondSimpleDto.DtoSecondOne(1234.654)

        val attrs = mapper.writeObject(data)

        val expectedData = mapOf(
            "double" to numberAttribute(1234.654),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDto\$SecondSimpleDto\$DtoSecondOne")
        )

        assertEquals(expectedData, attrs)
    }

    @Test
    fun testReadDtoBySealedClassInherited() {
        val dtoMap = mapOf(
            "double" to numberAttribute(1234.654),
            "dto_class_name" to stringAttribute("com.github.jaitl.dynamodb.mapper.helper.SimpleDto\$SecondSimpleDto\$DtoSecondOne")
        )

        val data = mapper.readObject(dtoMap, SimpleDto::class)

        val expectedData = SimpleDto.SecondSimpleDto.DtoSecondOne(1234.654)

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

    @Test(expected = RequiredFieldNotFoundException::class)
    fun testReadDTOWithoutClassNameField() {
        val dtoMap = mapOf(
            "string" to stringAttribute("test"),
        )
        val obj = mapOf(
            "dto" to mapAttribute(dtoMap),
        )

        mapper.readObject(obj, SimpleDataDto::class)
    }
}
