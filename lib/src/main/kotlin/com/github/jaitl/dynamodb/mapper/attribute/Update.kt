package com.github.jaitl.dynamodb.mapper

import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate

fun updateAttribute(attribute: AttributeValue, action: AttributeAction): AttributeValueUpdate =
    AttributeValueUpdate.builder().value(attribute).action(action).build()
