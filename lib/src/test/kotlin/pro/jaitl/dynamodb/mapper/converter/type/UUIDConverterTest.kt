package pro.jaitl.dynamodb.mapper.converter.type

import org.junit.Test
import pro.jaitl.dynamodb.mapper.Mapper
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import java.util.UUID
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
