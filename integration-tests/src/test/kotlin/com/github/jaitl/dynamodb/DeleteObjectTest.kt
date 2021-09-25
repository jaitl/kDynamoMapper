package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.DynamoDbTestSuite
import com.github.jaitl.dynamodb.base.TableConfig
import com.github.jaitl.dynamodb.base.helpCreateTable
import com.github.jaitl.dynamodb.base.helpPutItem
import com.github.jaitl.dynamodb.mapper.Mapper
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Instant
import kotlin.test.Test
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
