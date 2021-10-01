package pro.jaitl.dynamodb.mapper

import pro.jaitl.dynamodb.mapper.attribute.booleanAttribute
import pro.jaitl.dynamodb.mapper.attribute.mapAttribute
import pro.jaitl.dynamodb.mapper.attribute.numberAttribute
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriterTest {

    private val writer: KDynamoMapperWriter = Writer()

    @Test(expected = NotDataClassTypeException::class)
    fun testIsntDataType() {
        class SomeClass(val data: String)

        val simple = SomeClass("ddd")
        writer.writeObject(simple)
    }

    @Test(expected = UnknownTypeException::class)
    fun testUnknownType() {
        class SomeClass
        data class SimpleData(val some: SomeClass)

        val simple = SimpleData(SomeClass())
        writer.writeObject(simple)
    }

    @Test
    fun testNullField() {
        data class SimpleData(val str: String, val digit: Int?)

        val simple = SimpleData("ddd", null)
        val map = writer.writeObject(simple)
        val expectedMap = mapOf(
            "str" to stringAttribute("ddd")
        )
        assertEquals(expectedMap, map)
    }

    @Test
    fun testSimpleData() {
        data class SimpleData(val str: String, val digit: Int)

        val simple = SimpleData("ddd", 123)
        val map = writer.writeObject(simple)
        val expectedMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        assertEquals(expectedMap, map)
    }

    @Test
    fun testNestedDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        data class NestedData(val sometd: String, val data: SimpleData)

        val nested = NestedData("abc", SimpleData("ddd", 123))
        val map = writer.writeObject(nested)

        val simpleDataMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        val expectedMap = mapOf(
            "sometd" to stringAttribute("abc"),
            "data" to mapAttribute(simpleDataMap)
        )
        assertEquals(expectedMap, map)
    }

    @Test
    fun testCommonTypes() {
        data class TypeData(val id: UUID, val bool: Boolean, val inst: Instant)

        val data = TypeData(UUID.randomUUID(), true, Instant.now())

        val map = writer.writeObject(data)

        val expectedMap = mapOf(
            "id" to stringAttribute(data.id.toString()),
            "bool" to booleanAttribute(data.bool),
            "inst" to stringAttribute(DateTimeFormatter.ISO_INSTANT.format(data.inst))
        )

        assertEquals(expectedMap, map)
    }
}
