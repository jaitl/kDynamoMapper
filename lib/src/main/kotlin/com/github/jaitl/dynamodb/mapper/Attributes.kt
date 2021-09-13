package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()
internal fun numberAttribute(numb: Number): AttributeValue = AttributeValue.builder().n(numb.toString()).build()
internal fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue = AttributeValue.builder().m(map).build()
