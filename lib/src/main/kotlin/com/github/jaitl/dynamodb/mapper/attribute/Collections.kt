package com.github.jaitl.dynamodb.mapper.attribute

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
 * Wrapper for Map AttributeValue.m()
 */
fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue =
    AttributeValue.builder().m(map).build()

/**
 * Wrapper for List AttributeValue.l()
 */
fun listAttribute(list: List<AttributeValue>): AttributeValue =
    AttributeValue.builder().l(list).build()

/**
 * Wrapper for List AttributeValue.l()
 */
fun listAttribute(vararg list: AttributeValue): AttributeValue =
    AttributeValue.builder().l(*list).build()

/**
 * Wrapper for Set AttributeValue.l()
 */
fun setAttribute(list: List<AttributeValue>) = listAttribute(list)

/**
 * Wrapper for Set AttributeValue.l()
 */
fun setAttribute(vararg list: AttributeValue) = listAttribute(*list)

/**
 * Wrapper for String Set AttributeValue.ss()
 */
fun stringSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ss(set).build()

/**
 * Wrapper for String Set AttributeValue.ss()
 */
fun stringSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ss(*set).build()

/**
 * Wrapper for Number Set AttributeValue.ns()
 */
fun numberSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ns(set).build()

/**
 * Wrapper for Number Set AttributeValue.ns()
 */
fun numberSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ns(*set).build()
