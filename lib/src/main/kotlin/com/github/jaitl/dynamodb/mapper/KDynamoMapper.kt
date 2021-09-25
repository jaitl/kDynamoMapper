package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Interface provides a writing functionality. Implementation will allow you to map
 * a case class to Map<String, AttributeValue> which DynamoDbClient can consume.
 */
interface KDynamoMapperWriter {
    /**
     * Writes any object of a case class to Map<String, AttributeValue>.
     *
     * @param obj instance of a case class.
     * @return mapped instance of Map<String, AttributeValue>.
     */
    fun writeObject(obj: Any): Map<String, AttributeValue>

    /**
     * Writes any value to AttributeValue.
     * It is an internal method used for recursive writing of nested case classes and collections.
     * You can use it when you have KType for the value.
     *
     * @param value instance of any value. For example Int, String, Instant, etc.
     * @param kType type information about the value. You can get it
     *              from the experimental typeof<> function.
     *              For example typeof<String>.
     * @return mapped instance of AttributeValue.
     */
    fun writeValue(value: Any, kType: KType): AttributeValue
}

/**
 * Interface provides a reading functionality. Implementation will allow you to map an object of
 * Map<String, AttributeValue> to the required case class.
 */
interface KDynamoMapperReader {
    /**
     * Reads Map<String, AttributeValue> to a case class.
     *
     * @param obj an instance of Map<String, AttributeValue>.
     * @param clazz a class reference for the returned value. You can get it by ::class.
     *              For example String::class.
     *
     * @return mapped instance of the required data class.
     */
    fun <T : Any> readObject(obj: Map<String, AttributeValue>, clazz: KClass<T>): T

    /**
     * Reads AttributeValue to any class.
     * It is an internal method used for recursive reading of nested case classes and collections.
     * You can use it when you have KType for the returned value.
     *
     * @param attr instance of AttributeValue.
     * @param kType type information about the returned value.
     *              You can get it from the experimental typeof<> function. For example typeof<String>.
     * @return mapped instance of value with type from kType.
     */
    fun readValue(attr: AttributeValue, kType: KType): Any
}

/**
 * Composite interface that union KDynamoMapperWriter and KDynamoMapperWriter interfaces for simplicity.
 */
interface KDynamoMapper : KDynamoMapperReader, KDynamoMapperWriter
