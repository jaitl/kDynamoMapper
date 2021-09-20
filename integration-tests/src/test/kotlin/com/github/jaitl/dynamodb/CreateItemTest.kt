package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.DynamoDbTestSuite
import com.github.jaitl.dynamodb.base.TableConfig
import com.github.jaitl.dynamodb.base.helpCreateTable
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.stringAttribute
import org.junit.Test
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Instant
import kotlin.test.assertEquals


internal class CreateItemTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    @Test
    fun test() {
        val mapper = Mapper()
        dynamoDbClient.helpCreateTable(table)

        data class MyData(val id: String, val dataInt: Int, val dataInstant: Instant)

        val expectedData = MyData("1", 1234, Instant.now())

        val dynamoData = mapper.writeObject(expectedData)

        val putRequest = PutItemRequest.builder()
            .tableName(table.tableName)
            .item(dynamoData)
            .build()

        dynamoDbClient.putItem(putRequest)

        val keyMap = mapOf("id" to stringAttribute(expectedData.id))

        val getRequest = GetItemRequest.builder()
            .key(keyMap)
            .tableName(table.tableName)
            .build();

        val result = dynamoDbClient.getItem(getRequest)

        val returnedData = mapper.readObject(result.item(), MyData::class)

        assertEquals(expectedData, returnedData)
    }
}
