package pro.jaitl.dynamodb.mapper

import pro.jaitl.dynamodb.mapper.converter.TypeConverter
import pro.jaitl.dynamodb.mapper.converter.collection.ListConverter
import pro.jaitl.dynamodb.mapper.converter.collection.MapConverter
import pro.jaitl.dynamodb.mapper.converter.collection.SetConverter
import pro.jaitl.dynamodb.mapper.converter.type.*
import kotlin.reflect.KClass


/**
 * Map with type converters who implement the TypeConverter interface.
 *
 * @param registry map with type converters.
 */
data class ConverterRegistry(val registry: Map<KClass<*>, TypeConverter<*>>)

/**
 * Map with default converters.
 */
val DEFAULT_CONVERTERS: Map<KClass<*>, TypeConverter<*>> = listOf(
    // collections
    ListConverter(),
    SetConverter(NumberConverter()),
    MapConverter(),

    // external types
    NumberConverter(),
    StringConverter(),
    BooleanConverter(),
    InstantConverter(),
    UUIDConverter(),
).associateBy { it.type() }

/**
 * Registry instance with default converters
 */
val DEFAULT_REGISTRY = ConverterRegistry(registry = DEFAULT_CONVERTERS)
