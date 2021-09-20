package com.github.jaitl.dynamodb.base

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest


internal abstract class DynamoDbTestSuite {
    private val containerName = DockerImageName.parse("amazon/dynamodb-local:1.16.0")
    private val dynamoDbContainer = GenericContainer<Nothing>(containerName)
        .apply { withExposedPorts(8000) }

    protected lateinit var dynamoDbClient: DynamoDbClient

    @BeforeTest
    fun start() {
        dynamoDbContainer.start()
        val host = "http://localhost:${dynamoDbContainer.getMappedPort(8000)}"
        dynamoDbClient = DynamoDbClient.builder()
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
    }

    @AfterTest
    fun stop() {
        dynamoDbContainer.stop()
    }
}
