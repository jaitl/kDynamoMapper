package pro.jaitl.dynamodb.mapper.attribute

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
 * Wrapper for String AttributeValue.s()
 */
fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()

/**
 * Wrapper for Number AttributeValue.n()
 */
fun numberAttribute(numb: Number): AttributeValue =
    AttributeValue.builder().n(numb.toString()).build()

/**
 * Wrapper for Boolean AttributeValue.bool()
 */
fun booleanAttribute(boolean: Boolean): AttributeValue =
    AttributeValue.builder().bool(boolean).build()
