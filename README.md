# kDynamoMapper
[![build](https://github.com/jaitl/kDynamoMapper/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/jaitl/kDynamoMapper/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/jaitl/kDynamoMapper/branch/main/graph/badge.svg?token=2JXCJZDUHQ)](https://codecov.io/gh/jaitl/kDynamoMapper)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f7d2b2905373454fa647777ec2377957)](https://www.codacy.com/gh/jaitl/kDynamoMapper/dashboard)

[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/jaitl/kDynamoMapper/blob/main/LICENSE)

Lightweight AWS DynamoDB mapper for Kotlin written in pure Kotlin.

It exists because I haven't found a mapper that supports immutable data classes.

***kDynamoMapper*** supports [_AWS SDK for Java 2.x_](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb.html) only.

***kDynamoMapper*** maps a `data class` to [AttributeValue](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) and vice versa.
***kDynamoMapper*** doesn't wrap `DynamoDbClient`. You have to use original `DynamoDbClient` from [AWS SDK for Java 2.0](https://github.com/aws/aws-sdk-java-v2) to work with DynamoDB.

## Installation
Artifact is being deployed to maven central.

## Usage
***kDynamoMapper*** supports data classes only. When a data class contains another data class as property it will be mapped as well.

### Mapper creating
```kotlin
val mapper: KDynamoMapper = Mapper()
```

### Data classes for examples
```kotlin
data class NestedObject(val dataDouble: Double, val dataInstant: Instant)
data class MyData(val id: String, val dataInt: Int, val nested: NestedObject)
data class MyKey(val id: String)
```

### Writing
```kotlin
val data = MyData("1", 1234, NestedObject(333.33, Instant.now()))

val dynamoData = mapper.writeObject(data)

val putRequest = PutItemRequest.builder()
    .tableName(table.tableName)
    .item(dynamoData)
    .build()

dynamoDbClient.putItem(putRequest)
```

### Reading
```kotlin
val keyValue = mapper.writeObject(MyKey(data.id))

val getRequest = GetItemRequest.builder()
    .key(keyValue)
    .tableName(table.tableName)
    .build()

val result = dynamoDbClient.getItem(getRequest)

val data = mapper.readObject(result.item(), MyData::class)
```

### Updating a value
```kotlin
val itemKey = mapper.writeObject(MyKey("1"))

val updatedValues = mapOf(
    "dataInt" to updateAttribute(
        attribute = numberAttribute(4321),
        action = AttributeAction.PUT
    )
)

val updateRequest = UpdateItemRequest.builder()
    .tableName(table.tableName)
    .key(itemKey)
    .attributeUpdates(updatedValues)
    .build()

dynamoDbClient.updateItem(updateRequest)
```

### Updating a nested object
```kotlin
val itemKey = mapper.writeObject(MyKey("1"))
val newNested = NestedObject(4321.33, Instant.now().plusSeconds(1000))

val updatedValues = mapOf(
    "nested" to updateAttribute(
        attribute = mapAttribute(mapper.writeObject(newNested)),
        action = AttributeAction.PUT
    )
)

val updateRequest = UpdateItemRequest.builder()
    .tableName(table.tableName)
    .key(itemKey)
    .attributeUpdates(updatedValues)
    .build()

dynamoDbClient.updateItem(updateRequest)
```

You can run and play with the examples in integration tests.

## ADT supported
ADT are determined by inheritance from a sealed interface/class. 
Each ADT contains the 'adt_class_name' field with the original class name.

## Set up a custom converter


## Contribution
1. There are several [opened issues](https://github.com/jaitl/kDynamoMapper/issues). When you want to resolve an opened issue don't forget to write about it in the issue.
2. If there isn't a needed converter for you feel free to open [a new issue](https://github.com/jaitl/kDynamoMapper/issues/new) then implement and contribute the converter.
