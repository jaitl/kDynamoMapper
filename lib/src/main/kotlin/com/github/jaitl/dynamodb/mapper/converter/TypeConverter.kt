package com.github.jaitl.dynamodb.mapper.converter

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface TypeConverter<T : Any> {
    fun read(attr: AttributeValue, kType: KType): T
    fun write(value: Any, kType: KType): AttributeValue
    fun type(): KClass<T>
}
