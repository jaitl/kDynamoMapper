package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.*
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.attribute.numberAttribute
import com.github.jaitl.dynamodb.mapper.updateAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EditObjectTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper = Mapper()

    @Test
    fun testUpdateValue() {
        dynamoDbClient.helpCreateTable(table)

        data class MyData(val id: String, val dataInt: Int, val dataInstant: Instant)
        data class MyKey(val id: String)

        val data = MyData("1", 1234, Instant.now())

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))

        val updatedValues = mapOf(
            "dataInt" to updateAttribute(
                attribute = numberAttribute(4321),
                action = AttributeAction.PUT
            )
        )

        val updateRequest = UpdateItemRequest.builder()
            .tableName(table.tableName)
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build()

        dynamoDbClient.updateItem(updateRequest)

        val updatedItem = dynamoDbClient.helpGetItem(MyKey("1"), table.tableName, MyData::class)

        assertEquals(data.copy(dataInt = 4321), updatedItem)
    }

    @Test
    fun testUpdateObject() {
        dynamoDbClient.helpCreateTable(table)

        data class NestedObject(val dataInt: Int, val dataInstant: Instant)
        data class MyData(val id: String, val nested: NestedObject)
        data class MyKey(val id: String)

        val data = MyData("1", NestedObject(1234, Instant.now()))

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))
        val newNested = NestedObject(4321, data.nested.dataInstant.plusSeconds(1000))

        val updatedValues = mapOf(
            "nested" to updateAttribute(
                attribute = mapAttribute(mapper.writeObject(newNested)),
                action = AttributeAction.PUT
            )
        )

        val updateRequest = UpdateItemRequest.builder()
            .tableName(table.tableName)
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build()

        dynamoDbClient.updateItem(updateRequest)

        val updatedItem = dynamoDbClient.helpGetItem(MyKey("1"), table.tableName, MyData::class)

        assertEquals(MyData("1", nested = newNested), updatedItem)
    }

    @Test
    fun testUpdateAdt() {
        dynamoDbClient.helpCreateTable(table)

        val data = MyClass("1", Adt.AdtOne(1234, "one one"))

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))
        val updatedAdt = Adt.AdtTwo(4321L, Instant.now(), 4444.0)

        val updatedValues = mapOf(
            "adt" to updateAttribute(
                attribute = mapAttribute(mapper.writeObject(updatedAdt)),
                action = AttributeAction.PUT
            )
        )

        val updateRequest = UpdateItemRequest.builder()
            .tableName(table.tableName)
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build()

        dynamoDbClient.updateItem(updateRequest)

        val updatedItem = dynamoDbClient.helpGetItem(MyKey("1"), table.tableName, MyClass::class)

        val expectedItem = data.copy(adt = updatedAdt)

        assertEquals(expectedItem, updatedItem)
    }
}
