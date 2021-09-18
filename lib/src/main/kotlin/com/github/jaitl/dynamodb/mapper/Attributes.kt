package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()
internal fun numberAttribute(numb: Number): AttributeValue =
    AttributeValue.builder().n(numb.toString()).build()

internal fun booleanAttribute(boolean: Boolean): AttributeValue =
    AttributeValue.builder().bool(boolean).build()

internal fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue =
    AttributeValue.builder().m(map).build()

internal fun listAttribute(list: List<AttributeValue>): AttributeValue =
    AttributeValue.builder().l(list).build()

internal fun listAttribute(vararg list: AttributeValue): AttributeValue =
    AttributeValue.builder().l(*list).build()

internal fun setAttribute(list: List<AttributeValue>) = listAttribute(list)
internal fun setAttribute(vararg list: AttributeValue) = listAttribute(*list)
internal fun stringSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ss(set).build()

internal fun stringSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ss(*set).build()

internal fun numberSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ns(set).build()

internal fun numberSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ns(*set).build()
