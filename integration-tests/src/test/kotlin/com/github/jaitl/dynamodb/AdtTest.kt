package com.github.jaitl.dynamodb

import com.github.jaitl.dynamodb.base.DynamoDbTestSuite
import com.github.jaitl.dynamodb.base.TableConfig
import com.github.jaitl.dynamodb.base.helpCreateTable
import com.github.jaitl.dynamodb.mapper.Mapper
import com.github.jaitl.dynamodb.mapper.attribute.mapAttribute
import com.github.jaitl.dynamodb.mapper.updateAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class AdtTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper = Mapper()

    @Test
    fun testAdt() {
        dynamoDbClient.helpCreateTable(table)

        // put
        val data = MyAdtData("1", Adt.AdtOne(1234, "one one"))

        val dynamoData = mapper.writeObject(data)

        val putRequest = PutItemRequest.builder()
            .tableName(table.tableName)
            .item(dynamoData)
            .build()

        dynamoDbClient.putItem(putRequest)

        // update
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

        // get
        val keyValue = mapper.writeObject(MyKey("1"))

        val getRequest = GetItemRequest.builder()
            .key(keyValue)
            .tableName(table.tableName)
            .build()

        val result = dynamoDbClient.getItem(getRequest)

        val updatedItem = mapper.readObject(result.item(), MyAdtData::class)

        val expectedItem = data.copy(adt = updatedAdt)

        assertEquals(expectedItem, updatedItem)
    }
}
