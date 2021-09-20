package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()
fun numberAttribute(numb: Number): AttributeValue =
    AttributeValue.builder().n(numb.toString()).build()

fun booleanAttribute(boolean: Boolean): AttributeValue =
    AttributeValue.builder().bool(boolean).build()

fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue =
    AttributeValue.builder().m(map).build()

fun listAttribute(list: List<AttributeValue>): AttributeValue =
    AttributeValue.builder().l(list).build()

fun listAttribute(vararg list: AttributeValue): AttributeValue =
    AttributeValue.builder().l(*list).build()

fun setAttribute(list: List<AttributeValue>) = listAttribute(list)
fun setAttribute(vararg list: AttributeValue) = listAttribute(*list)
fun stringSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ss(set).build()

fun stringSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ss(*set).build()

fun numberSetAttribute(set: Set<String>): AttributeValue =
    AttributeValue.builder().ns(set).build()

fun numberSetAttribute(vararg set: String): AttributeValue =
    AttributeValue.builder().ns(*set).build()
