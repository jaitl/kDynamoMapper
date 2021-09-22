package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.*
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.mapAttribute
import com.github.jaitl.dynamodb.mapper.numberAttribute
import com.github.jaitl.dynamodb.mapper.updateAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class DeleteObjectTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper = Mapper()

    @Test
    fun testDeleteValue() {
        dynamoDbClient.helpCreateTable(table)

        data class MyData(val id: String, val dataInt: Int, val dataInstant: Instant)
        data class MyKey(val id: String)

        val data = MyData("1", 1234, Instant.now())

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))

        val deleteReq = DeleteItemRequest.builder()
            .tableName(table.tableName)
            .key(itemKey)
            .build()

        dynamoDbClient.deleteItem(deleteReq)

        val getRequest = GetItemRequest.builder()
            .key(itemKey)
            .tableName(table.tableName)
            .build()

        val result = dynamoDbClient.getItem(getRequest)

        assertFalse(result.hasItem())
    }
}
