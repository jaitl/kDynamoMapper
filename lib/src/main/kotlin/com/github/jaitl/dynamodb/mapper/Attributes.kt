package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()
fun numberAttribute(numb: Number): AttributeValue = AttributeValue.builder().n(numb.toString()).build()
fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue = AttributeValue.builder().m(map).build()
