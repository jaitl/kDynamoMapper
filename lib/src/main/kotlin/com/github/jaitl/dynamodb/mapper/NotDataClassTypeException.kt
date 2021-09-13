package com.github.jaitl.dynamodb.mapper

import kotlin.reflect.KClass

class NotDataClassTypeException(clazz: KClass<*>): Exception("Type '${clazz}' isn't data class type")
