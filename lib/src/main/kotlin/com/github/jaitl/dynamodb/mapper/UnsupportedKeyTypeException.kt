package com.github.jaitl.dynamodb.mapper

import kotlin.reflect.KClass

class UnsupportedKeyTypeException(clazz: KClass<*>): Exception("Map doesn't support type '${clazz}' as key. Only 'String' type supported as map's key")