package com.github.jaitl.dynamodb

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.core.waiters.WaiterResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


class DynamoDbCreateTableTest {
    private val containerName = DockerImageName.parse("amazon/dynamodb-local:1.16.0")
    val dynamoDb = GenericContainer<Nothing>(containerName).apply { withExposedPorts(8000) }

    @BeforeTest
    fun start() {
        dynamoDb.start()
    }

    @AfterTest
    fun stop() {
        dynamoDb.stop()
    }

    @Test
    fun test() {
        val client = DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:${dynamoDb.getMappedPort(8000)}"))
            .region(Region.US_WEST_2)
            .build()

        val dbWaiter = client.waiter()

        val key = "id"
        val tableName = "test"

        val request: CreateTableRequest = CreateTableRequest.builder()
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName(key)
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName(key)
                    .keyType(KeyType.HASH)
                    .build()
            )
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(10)
                    .writeCapacityUnits(10)
                    .build()
            )
            .tableName(tableName)
            .build()

        val response: CreateTableResponse = client.createTable(request)
        
        val tableRequest = DescribeTableRequest.builder()
            .tableName(tableName)
            .build()

        val waiterResponse: WaiterResponse<DescribeTableResponse> =
            dbWaiter.waitUntilTableExists(tableRequest)
        waiterResponse.matched().response().ifPresent { x: DescribeTableResponse? ->
            println(
                x
            )
        }
    }

}