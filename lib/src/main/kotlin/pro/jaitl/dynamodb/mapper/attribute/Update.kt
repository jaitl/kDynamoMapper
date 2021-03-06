package pro.jaitl.dynamodb.mapper.attribute

import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate

/**
 * Wrapper for AttributeValueUpdate
 */
fun updateAttribute(attribute: AttributeValue, action: AttributeAction): AttributeValueUpdate =
    AttributeValueUpdate.builder().value(attribute).action(action).build()
