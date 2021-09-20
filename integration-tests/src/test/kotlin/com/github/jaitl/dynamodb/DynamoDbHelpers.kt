package com.github.jaitl.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import kotlin.test.assertEquals

data class TableConfig(val tableName: String, val keyName: String)

fun DynamoDbClient.createTable(tableConfig: TableConfig) {
    val request: CreateTableRequest = CreateTableRequest.builder()
        .attributeDefinitions(
            AttributeDefinition.builder()
                .attributeName(tableConfig.keyName)
                .attributeType(ScalarAttributeType.S)
                .build()
        )
        .keySchema(
            KeySchemaElement.builder()
                .attributeName(tableConfig.keyName)
                .keyType(KeyType.HASH)
                .build()
        )
        .provisionedThroughput(
            ProvisionedThroughput.builder()
                .readCapacityUnits(10)
                .writeCapacityUnits(10)
                .build()
        )
        .tableName(tableConfig.tableName)
        .build()

    createTable(request)

    val tableRequest = DescribeTableRequest.builder()
        .tableName(tableConfig.tableName)
        .build()
    val waiterResponse = waiter().waitUntilTableExists(tableRequest)
    val tableDescription = waiterResponse.matched().response().orElseThrow()

    assertEquals(tableRequest.tableName(), tableDescription.table().tableName())
}
