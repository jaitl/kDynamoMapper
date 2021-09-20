package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.DynamoDbTestSuite
import com.github.jaitl.dynamodb.base.TableConfig
import com.github.jaitl.dynamodb.base.helpCreateTable
import com.github.jaitl.dynamodb.mapper.Mapper
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals


internal class GetAndPutObjectTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper = Mapper()

    @Test
    fun test() {
        dynamoDbClient.helpCreateTable(table)

        data class MyData(val id: String, val dataInt: Int, val dataInstant: Instant)
        data class MyKey(val id: String)

        val expectedData = MyData("1", 1234, Instant.now())

        val dynamoData = mapper.writeObject(expectedData)

        val putRequest = PutItemRequest.builder()
            .tableName(table.tableName)
            .item(dynamoData)
            .build()

        dynamoDbClient.putItem(putRequest)

        val keyValue = mapper.writeObject(MyKey(expectedData.id))

        val getRequest = GetItemRequest.builder()
            .key(keyValue)
            .tableName(table.tableName)
            .build();

        val result = dynamoDbClient.getItem(getRequest)

        val actualData = mapper.readObject(result.item(), MyData::class)

        assertEquals(expectedData, actualData)
    }
}
