package pro.jaitl.dynamodb.mapper

/**
 * Base class for all project exception.
 * Each exception in this project must be inherited form KDynamoMapperException class.
 */
sealed class KDynamoMapperException(message: String) : Exception(message)

/**
 * Throws when a map has unsupported key type.
 */
class UnsupportedKeyTypeException(message: String) : KDynamoMapperException(message)

/**
 * Throws when the user try to read or write a class other than data class.
 */
class NotDataClassTypeException(message: String) : KDynamoMapperException(message)

/**
 * Throws when there isn't a converter for the converted type in the registry.
 */
class UnknownTypeException(message: String) : KDynamoMapperException(message)

/**
 * Throws when a DynamoDb attribute map hasn't a required field for mapping a data class.
 */
class RequiredFieldNotFoundException(message: String, val fields: Set<String>) : KDynamoMapperException(message)
