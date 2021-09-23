package com.github.jaitl.dynamodb.mapper


sealed class KDynamoMapperException(message: String) : Exception(message)

class UnsupportedKeyTypeException(message: String) : KDynamoMapperException(message)

class NotDataClassTypeException(message: String) : KDynamoMapperException(message)

class UnknownTypeException(message: String) : KDynamoMapperException(message)

class RequiredFieldNotFoundException(message: String, val fields: Set<String>) : KDynamoMapperException(message)

class AttributeNotFoundException(message: String) : KDynamoMapperException(message)
