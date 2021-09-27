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

class AdtPutGetUpdateTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper = Mapper()

    @Test
    fun testAdt() {
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
