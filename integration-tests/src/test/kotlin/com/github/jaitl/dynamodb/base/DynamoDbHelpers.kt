package com.github.jaitl.dynamodb.base

import com.github.jaitl.dynamodb.mapper.Mapper
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI
import kotlin.reflect.KClass

internal data class TableConfig(val tableName: String, val keyName: String)

private val mapper = Mapper()

internal fun DynamoDbClient.helpCreateTable(tableConfig: TableConfig): CreateTableResponse {
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

    return createTable(request)
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

fun DynamoDbClient.helpPutItem(item: Any, table: String): PutItemResponse {
    val dynamoData = mapper.writeObject(item)

    val putRequest = PutItemRequest.builder()
        .tableName(table)
        .item(dynamoData)
        .build()

    return putItem(putRequest)
}

fun <T : Any> DynamoDbClient.helpGetItem(key: Any, table: String, clazz: KClass<T>): T {
    val keyValue = mapper.writeObject(key)

    val getRequest = GetItemRequest.builder()
        .key(keyValue)
        .tableName(table)
        .build();

    val result = getItem(getRequest)

    return mapper.readObject(result.item(), clazz)
}
