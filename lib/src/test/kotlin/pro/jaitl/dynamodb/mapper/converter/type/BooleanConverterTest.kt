package pro.jaitl.dynamodb.mapper.converter.type

import org.junit.Test
import pro.jaitl.dynamodb.mapper.Mapper
import pro.jaitl.dynamodb.mapper.attribute.booleanAttribute
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
