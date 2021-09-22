package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.*
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.mapAttribute
import com.github.jaitl.dynamodb.mapper.numberAttribute
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
            .build();

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
            .build();

        dynamoDbClient.updateItem(updateRequest)

        val updatedItem = dynamoDbClient.helpGetItem(MyKey("1"), table.tableName, MyData::class)

        assertEquals(MyData("1", nested = newNested), updatedItem)
    }

    @Test
    fun testUpdateDto() {
        dynamoDbClient.helpCreateTable(table)

        val dataOne = MyClassOne("1", DtoOne(1234, "one one"))

        dynamoDbClient.helpPutItem(dataOne, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))
        // TODO fix when real dto will be supported
        val dataTwo = MyClassTwo("1", DtoTwo(4321L, Instant.now(), 4444.0))

        val updatedValues = mapOf(
            "dto" to updateAttribute(
                attribute = mapAttribute(mapper.writeObject(dataTwo.dto)),
                action = AttributeAction.PUT
            )
        )

        val updateRequest = UpdateItemRequest.builder()
            .tableName(table.tableName)
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build();

        dynamoDbClient.updateItem(updateRequest)

        val updatedItem = dynamoDbClient.helpGetItem(MyKey("1"), table.tableName, MyClassTwo::class)

        assertEquals(dataTwo, updatedItem)
    }
}
