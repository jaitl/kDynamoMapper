package com.github.jaitl.dynamodb.base

import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("Wrappers")

fun logException(action: () -> Unit) =
    try {
        action()
    } catch (e: Exception) {
        logger.error("", e)
    }
