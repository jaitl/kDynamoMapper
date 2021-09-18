package com.github.jaitl.dynamodb.mapper

import com.github.jaitl.dynamodb.mapper.converter.TypeConverter
import com.github.jaitl.dynamodb.mapper.converter.collection.ListConverter
import com.github.jaitl.dynamodb.mapper.converter.collection.MapConverter
import com.github.jaitl.dynamodb.mapper.converter.collection.SetConverter
import com.github.jaitl.dynamodb.mapper.converter.type.*
import kotlin.reflect.KClass

data class ConverterRegistry(val registry: Map<KClass<*>, TypeConverter<*>>)

val DEFAULT_CONVERTERS: Map<KClass<*>, TypeConverter<*>> = listOf(
    //collections
    ListConverter(),
    SetConverter(NumberConverter()),
    MapConverter(),
    // types
    NumberConverter(),
    StringConverter(),
    BooleanConverter(),
    InstantConverter(),
    UUIDConverter(),
).associateBy { it.type() }

val DEFAULT_REGISTRY = ConverterRegistry(registry = DEFAULT_CONVERTERS)
