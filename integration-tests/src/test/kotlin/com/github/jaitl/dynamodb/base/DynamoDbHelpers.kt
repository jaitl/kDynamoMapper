package com.github.jaitl.dynamodb.base

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

internal data class TableConfig(val tableName: String, val keyName: String)

internal fun DynamoDbClient.helpCreateTable(tableConfig: TableConfig) = logException {
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
}
