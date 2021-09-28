package pro.jaitl.dynamodb.mapper.converter.collection

import pro.jaitl.dynamodb.mapper.Mapper
import pro.jaitl.dynamodb.mapper.attribute.listAttribute
import pro.jaitl.dynamodb.mapper.attribute.mapAttribute
import pro.jaitl.dynamodb.mapper.attribute.numberAttribute
import pro.jaitl.dynamodb.mapper.attribute.stringAttribute
import kotlin.test.Test
import kotlin.test.assertEquals


internal class ListConverterTest {
    val mapper = Mapper()

    @Test
    fun testWriteEmptyList() {
        data class Data(val list: List<String>)

        val data = Data(emptyList())

        val map = mapper.writeObject(data)

        val list = listAttribute()
        val expectedMap = mapOf("list" to list)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadEmptyList() {
        data class Data(val list: List<String>)

        val expectedData = Data(emptyList())

        val list = listAttribute()
        val obj = mapOf("list" to list)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteStringList() {
        data class Data(val list: List<String>)

        val data = Data(listOf("1", "2", "3"))

        val map = mapper.writeObject(data)

        val list = listAttribute(
            stringAttribute("1"),
            stringAttribute("2"),
            stringAttribute("3"),
        )
        val expectedMap = mapOf("list" to list)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadStringList() {
        data class Data(val list: List<String>)

        val expectedData = Data(listOf("1", "2", "3"))

        val list = listAttribute(
            stringAttribute("1"),
            stringAttribute("2"),
            stringAttribute("3"),
        )
        val obj = mapOf("list" to list)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }

    @Test
    fun testWriteDataClassList() {
        data class SimpleData(val vvv: Int)
        data class Data(val list: List<SimpleData>)

        val data = Data(listOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val map = mapper.writeObject(data)

        val list = listAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val expectedMap = mapOf("list" to list)

        assertEquals(expectedMap, map)
    }

    @Test
    fun testReadDataClassList() {
        data class SimpleData(val vvv: Int)
        data class Data(val list: List<SimpleData>)

        val expectedData = Data(listOf(SimpleData(1), SimpleData(2), SimpleData(3)))

        val list = listAttribute(
            mapAttribute(mapOf("vvv" to numberAttribute(1))),
            mapAttribute(mapOf("vvv" to numberAttribute(2))),
            mapAttribute(mapOf("vvv" to numberAttribute(3))),
        )
        val obj = mapOf("list" to list)

        val data = mapper.readObject(obj, Data::class)

        assertEquals(expectedData, data)
    }
}
