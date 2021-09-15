package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.util.*

internal fun stringAttribute(str: String): AttributeValue = AttributeValue.builder().s(str).build()
internal fun numberAttribute(numb: Number): AttributeValue = AttributeValue.builder().n(numb.toString()).build()
internal fun uuidAttribute(uuid: UUID): AttributeValue = AttributeValue.builder().s(uuid.toString()).build()
internal fun booleanAttribute(boolean: Boolean): AttributeValue = AttributeValue.builder().bool(boolean).build()
internal fun instantAttribute(instant: Instant): AttributeValue = AttributeValue.builder().s(ISO_INSTANT.format(instant)).build()
internal fun mapAttribute(map: Map<String, AttributeValue>): AttributeValue = AttributeValue.builder().m(map).build()
internal fun listAttribute(list: List<AttributeValue>): AttributeValue = AttributeValue.builder().l(list).build()
internal fun listAttribute(vararg list: AttributeValue): AttributeValue = AttributeValue.builder().l(*list).build()
