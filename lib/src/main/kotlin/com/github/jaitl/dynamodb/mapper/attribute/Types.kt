package com.github.jaitl.dynamodb.mapper.attribute

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()

fun numberAttribute(numb: Number): AttributeValue =
    AttributeValue.builder().n(numb.toString()).build()

fun booleanAttribute(boolean: Boolean): AttributeValue =
    AttributeValue.builder().bool(boolean).build()
