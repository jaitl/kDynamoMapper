package pro.jaitl.dynamodb

import pro.jaitl.dynamodb.base.*
import pro.jaitl.dynamodb.base.DynamoDbTestSuite
import pro.jaitl.dynamodb.base.TableConfig
import pro.jaitl.dynamodb.base.helpCreateTable
import pro.jaitl.dynamodb.mapper.KDynamoMapper
import pro.jaitl.dynamodb.mapper.Mapper
import pro.jaitl.dynamodb.mapper.attribute.updateAttribute
import pro.jaitl.dynamodb.mapper.writeValue
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


class CRUDTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    private val mapper: KDynamoMapper = Mapper()

    data class NestedObject(val dataDouble: Double, val dataInstant: Instant)
    data class MyData(val id: String, val dataInt: Int, val nested: NestedObject)
    data class MyKey(val id: String)

    @Test
    fun testPutAndGetObject() {
        dynamoDbClient.helpCreateTable(table)

        // put
        val data = MyData("1", 1234, NestedObject(333.33, Instant.now()))

        val dynamoData = mapper.writeObject(data)

        val putRequest = PutItemRequest.builder()
            .tableName(table.tableName)
            .item(dynamoData)
            .build()

        dynamoDbClient.putItem(putRequest)

        // get
        val keyValue = mapper.writeObject(MyKey(data.id))

        val getRequest = GetItemRequest.builder()
            .key(keyValue)
            .tableName(table.tableName)
            .build()

        val result = dynamoDbClient.getItem(getRequest)

        val actualData = mapper.readObject(result.item(), MyData::class)

        assertEquals(data, actualData)
    }

    @Test
    fun testUpdateValue() {
        dynamoDbClient.helpCreateTable(table)

        val data = MyData("1", 1234, NestedObject(333.33, Instant.now()))

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))

        val updatedValues = mapOf(
            "dataInt" to updateAttribute(
                attribute = mapper.writeValue(4321),
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

        val data = MyData("1", 1234, NestedObject(333.33, Instant.now()))

        dynamoDbClient.helpPutItem(data, table.tableName)

        val itemKey = mapper.writeObject(MyKey("1"))
        val newNested = NestedObject(4321.33, Instant.now().plusSeconds(1000))

        val updatedValues = mapOf(
            "nested" to updateAttribute(
                attribute = mapper.writeValue(newNested),
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

        val expectedItem = data.copy(nested = newNested)

        assertEquals(expectedItem, updatedItem)
    }

    @Test
    fun testDeleteObject() {
        dynamoDbClient.helpCreateTable(table)

        val data = MyData("1", 1234, NestedObject(333.33, Instant.now()))

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
