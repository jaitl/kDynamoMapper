package pro.jaitl.dynamodb.mapper

import pro.jaitl.dynamodb.mapper.attribute.booleanAttribute
import pro.jaitl.dynamodb.mapper.attribute.mapAttribute
import pro.jaitl.dynamodb.mapper.attribute.numberAttribute
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ReaderTest {

    private val reader: KDynamoMapperReader = Reader()

    @Test(expected = NotDataClassTypeException::class)
    fun testIsntDataType() {
        class SomeClass(val data: String)

        reader.readObject(emptyMap(), SomeClass::class)
    }

    @Test(expected = UnknownTypeException::class)
    fun testUnknownType() {
        class SomeClass
        data class SimpleData(val some: SomeClass)

        val obj = mapOf(
            "some" to stringAttribute("ddd")
        )

        reader.readObject(obj, SimpleData::class)
    }

    @Test
    fun testNotFoundField() {
        data class SimpleData(val string: String)

        val obj = mapOf<String, AttributeValue>()

        val exception = assertFailsWith(RequiredFieldNotFoundException::class) {
            reader.readObject(obj, SimpleData::class)
        }

        assertEquals(setOf("string"), exception.fields)
    }

    @Test
    fun testNotFoundOneField() {
        data class SimpleData(val string: String, val int: Int, val long: Long?)

        val obj = mapOf("string" to stringAttribute("value"))

        val exception = assertFailsWith(RequiredFieldNotFoundException::class) {
            reader.readObject(obj, SimpleData::class)
        }

        assertEquals(setOf("int"), exception.fields)
    }

    @Test
    fun testNullField() {
        data class SimpleData(val str: String, val digit: Int?)

        val expectedData = SimpleData("ddd", null)
        val obj = mapOf(
            "str" to stringAttribute("ddd")
        )

        val data = reader.readObject(obj, SimpleData::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testSimpleDataClass() {
        data class SimpleData(val str: String, val digit: Int)

        val expectedData = SimpleData("qwerty", 123)
        val obj = mapOf(
            "str" to stringAttribute("qwerty"),
            "digit" to numberAttribute(123)
        )

        val data = reader.readObject(obj, SimpleData::class)
        assertEquals(expectedData, data)
    }

    @Test
    fun testNestedSimpleDataClass() {
        data class SimpleData(val str: String, val digit: Int)
        data class NestedData(val sometd: String, val data: SimpleData)

        val nestedExpected = NestedData("abc", SimpleData("ddd", 123))
        val simpleDataMap = mapOf(
            "str" to stringAttribute("ddd"),
            "digit" to numberAttribute(123)
        )
        val obj = mapOf(
            "sometd" to stringAttribute("abc"),
            "data" to mapAttribute(simpleDataMap)
        )

        val nested = reader.readObject(obj, NestedData::class)

        assertEquals(nestedExpected, nested)
    }

    @Test
    fun testCommonTypes() {
        data class TypeData(val id: UUID, val bool: Boolean, val inst: Instant)

        val dataExpected = TypeData(UUID.randomUUID(), true, Instant.now())

        val obj = mapOf(
            "id" to stringAttribute(dataExpected.id.toString()),
            "bool" to booleanAttribute(dataExpected.bool),
            "inst" to stringAttribute(DateTimeFormatter.ISO_INSTANT.format(dataExpected.inst))
        )

        val data = reader.readObject(obj, TypeData::class)

        assertEquals(dataExpected, data)
    }
}
