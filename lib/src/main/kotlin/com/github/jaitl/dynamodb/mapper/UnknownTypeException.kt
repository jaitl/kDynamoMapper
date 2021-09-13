package com.github.jaitl.dynamodb.mapper

import kotlin.reflect.KClassifier

class UnknownTypeException(classifier: KClassifier?) : Exception("Unknown type: $classifier")