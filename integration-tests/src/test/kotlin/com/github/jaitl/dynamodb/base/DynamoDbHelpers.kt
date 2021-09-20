package com.github.jaitl.dynamodb.base

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI

internal data class TableConfig(val tableName: String, val keyName: String)

internal fun DynamoDbClient.helpCreateTable(tableConfig: TableConfig) {
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

fun helpCreateDynamoDbClient(host: String): DynamoDbClient =
    DynamoDbClient.builder()
        .endpointOverride(URI.create(host))
        .region(Region.US_WEST_2)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    "test",
                    "test"
                )
            )
        )
        .build()
