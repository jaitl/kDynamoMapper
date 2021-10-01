package pro.jaitl.dynamodb.mapper

import pro.jaitl.dynamodb.mapper.attribute.mapAttribute
import pro.jaitl.dynamodb.mapper.attribute.numberAttribute
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import pro.jaitl.dynamodb.mapper.helper.SimpleAdt
import pro.jaitl.dynamodb.mapper.helper.SimpleDataAdt
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AdtTest {

    val mapper = Mapper()

    @Test
    fun testWriteSimpleAdt() {
        val data = SimpleDataAdt(SimpleAdt.AdtOne("test"))

        val map = mapper.writeObject(data)

        val adtMap = mapOf(
            "string" to stringAttribute("test"),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleAdt\$AdtOne")
        )
        val expectedMap = mapOf(
            "adt" to mapAttribute(adtMap),
        )

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadSimpleAdt() {
        val adtMap = mapOf(
            "string" to stringAttribute("test"),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleAdt\$AdtOne")
        )
        val obj = mapOf(
            "adt" to mapAttribute(adtMap),
        )

        val data = mapper.readObject(obj, SimpleDataAdt::class)

        val expectedData = SimpleDataAdt(SimpleAdt.AdtOne("test"))

        assertEquals(expectedData, data)
    }

    @Test
    fun testReadAdtBySealedClass() {
        val adtMap = mapOf(
            "long" to numberAttribute(1234),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleAdt\$AdtTwo")
        )

        val data = mapper.readObject(adtMap, SimpleAdt::class)

        val expectedData = SimpleAdt.AdtTwo(1234)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteAdtBySealedClassInherited() {
        val data = SimpleAdt.SecondSimpleAdt.AdtSecondOne(1234.654)

        val attrs = mapper.writeObject(data)

        val expectedData = mapOf(
            "double" to numberAttribute(1234.654),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleAdt\$SecondSimpleAdt\$AdtSecondOne")
        )

        assertEquals(expectedData, attrs)
    }

    @Test
    fun testReadAdtBySealedClassInherited() {
        val adtMap = mapOf(
            "double" to numberAttribute(1234.654),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleAdt\$SecondSimpleAdt\$AdtSecondOne")
        )

        val data = mapper.readObject(adtMap, SimpleAdt::class)

        val expectedData = SimpleAdt.SecondSimpleAdt.AdtSecondOne(1234.654)

        assertEquals(expectedData, data)
    }

    @Test(expected = UnknownTypeException::class)
    fun testReadAdtBySealedClassWrongClass() {
        val adtMap = mapOf(
            "long" to numberAttribute(1234),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.SimpleDataAdt")
        )

        mapper.readObject(adtMap, SimpleAdt::class)
    }

    @Test(expected = ClassNotFoundException::class)
    fun testReadNotExistsClass() {
        val adtMap = mapOf(
            "string" to stringAttribute("test"),
            "adt_class_name" to stringAttribute("pro.jaitl.dynamodb.mapper.helper.Adt")
        )
        val obj = mapOf(
            "adt" to mapAttribute(adtMap),
        )

        mapper.readObject(obj, SimpleDataAdt::class)
    }

    @Test(expected = RequiredFieldNotFoundException::class)
    fun testReadAdtWithoutClassNameField() {
        val adtMap = mapOf(
            "string" to stringAttribute("test"),
        )
        val obj = mapOf(
            "adt" to mapAttribute(adtMap),
        )

        mapper.readObject(obj, SimpleDataAdt::class)
    }
}
