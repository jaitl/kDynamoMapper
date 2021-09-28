package pro.jaitl.dynamodb

import pro.jaitl.dynamodb.base.DynamoDbTestSuite
import pro.jaitl.dynamodb.base.TableConfig
import pro.jaitl.dynamodb.base.helpCreateTable
import pro.jaitl.dynamodb.mapper.ConverterRegistry
import pro.jaitl.dynamodb.mapper.DEFAULT_CONVERTERS
import pro.jaitl.dynamodb.mapper.Mapper
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomConverterTest : DynamoDbTestSuite() {
    private val table = TableConfig("table", "id")

    @Test
    fun testPutAndGetObject() {
        dynamoDbClient.helpCreateTable(table)

        val customConvertersMap = listOf(SimpleDataTypeConverter(), ComplexDataTypeConverter())
            .associateBy { it.type() }
        val registry = ConverterRegistry(DEFAULT_CONVERTERS + customConvertersMap)

        val mapper = Mapper(registry)

        // put
        val data = MyDataClass(
            id = "1",
            simpleDataType = SimpleDataType(Instant.now()),
            complexDataType = ComplexDataType("test", 1234, SimpleDataType(Instant.now().plusSeconds(1000)))
        )

        val dynamoData = mapper.writeObject(data)

        val putRequest = PutItemRequest.builder()
            .tableName(table.tableName)
            .item(dynamoData)
            .build()

        dynamoDbClient.putItem(putRequest)

        // get
        val keyValue = mapper.writeObject(MyDataKey(data.id))

        val getRequest = GetItemRequest.builder()
            .key(keyValue)
            .tableName(table.tableName)
            .build()

        val result = dynamoDbClient.getItem(getRequest)

        val actualData = mapper.readObject(result.item(), MyDataClass::class)

        assertEquals(data, actualData)
    }
}
