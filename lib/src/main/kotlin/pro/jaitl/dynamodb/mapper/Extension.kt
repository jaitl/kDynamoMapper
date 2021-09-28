package pro.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.reflect.full.createType

/**
 * Writes any value to a DynamoDb attribute.
 * Wrapper for original writeValue method from KDynamoMapperWriter that simplifies creating KType.
 *
 * ATTENTION!!! This method doesn't support generics types!
 * Support for generics types will be added when the method typeof<> become stable.
 * According to Kotlin RoadMap it will be in the 1.6 version.
 */
inline fun <reified T : Any> KDynamoMapperWriter.writeValue(value: T): AttributeValue {
    val kType = T::class.createType()
    return writeValue(value, kType)
}

/**
 * Reads a DynamoDb attribute to a type.
 * Wrapper for original readValue method from KDynamoMapperReader that simplifies creating KType.
 *
 * ATTENTION!!! This method doesn't support generics types!
 * Support for generics types will be added when the method typeof<> become stable.
 * According to Kotlin RoadMap it will be in the 1.6 version.
 */
inline fun <reified T : Any> KDynamoMapperReader.readValue(attr: AttributeValue): T {
    val kType = T::class.createType()
    return readValue(attr, kType) as T
}
