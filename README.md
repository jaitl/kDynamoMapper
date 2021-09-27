# kDynamoMapper
Lightweight AWS DynamoDB mapper for Kotlin written in pure Kotlin.

[![build](https://github.com/jaitl/kDynamoMapper/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/jaitl/kDynamoMapper/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/jaitl/kDynamoMapper/branch/main/graph/badge.svg?token=2JXCJZDUHQ)](https://codecov.io/gh/jaitl/kDynamoMapper)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f7d2b2905373454fa647777ec2377957)](https://www.codacy.com/gh/jaitl/kDynamoMapper/dashboard)

[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/jaitl/kDynamoMapper/blob/main/LICENSE)

***kDynamoMapper*** exists because I haven't found a mapper that supports immutable data classes.

***kDynamoMapper*** supports [_AWS SDK for Java 2.x_](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb.html) only.

***kDynamoMapper*** maps a `data class` to [AttributeValue](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) and vice versa.
***kDynamoMapper*** doesn't wrap `DynamoDbClient`. You have to use original `DynamoDbClient` from `AWS SDK 2` to work with DynamoDB.

## Installation
Artifact is being deployed to maven central.

## Usage
kDynamoMapper supports data classes only. When a data class contains another data class as property it will be mapped as well.

### Read a data class:


### Write a data class:

More examples you can find in tests and integration tests.

### ADT
ADT are determined by inheritance from a sealed interface/class. 
Each ADT contains the 'adt_class_name' field with the original class name.

ADT example:
Read:
Write:

### Set up a custom converter


## Contribution
1. There are several [opened issues](https://github.com/jaitl/kDynamoMapper/issues). When you want to resolve an opened issue don't forget to write about it in the issue.
2. If there isn't a needed converter for you feel free to open [a new issue](https://github.com/jaitl/kDynamoMapper/issues/new) then implement and contribute the converter.
