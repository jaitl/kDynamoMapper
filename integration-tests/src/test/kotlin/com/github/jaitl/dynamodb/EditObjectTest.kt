package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.*
import com.github.jaitl.dynamodb.base.DynamoDbTestSuite
import com.github.jaitl.dynamodb.base.TableConfig
import com.github.jaitl.dynamodb.base.helpCreateTable
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.numberAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EditObjectTest: DynamoDbTestSuite() {
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
            "dataInt" to AttributeValueUpdate.builder()
                .value(numberAttribute(4321))
                .action(AttributeAction.PUT)
                .build()
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

        val result = dynamoDbClient.helpPutItem(data, table.tableName)
    }
}
