package pro.jaitl.dynamodb.base

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class DynamoDbTestSuite {
    private val containerName = DockerImageName.parse("amazon/dynamodb-local:1.16.0")
    private val dynamoDbContainer = GenericContainer<Nothing>(containerName)
        .apply { withExposedPorts(8000) }

    protected lateinit var dynamoDbClient: DynamoDbClient

    @BeforeTest
    fun start() {
        dynamoDbContainer.start()
        val host = "http://localhost:${dynamoDbContainer.getMappedPort(8000)}"
        dynamoDbClient = helpCreateDynamoDbClient(host)
    }

    @AfterTest
    fun stop() {
        dynamoDbContainer.stop()
    }
}
