package com.github.jaitl.dynamodb

import java.time.Instant

sealed class Dto {
    data class DtoOne(val int: Int, val string: String) : Dto()
    data class DtoTwo(val long: Long, val instant: Instant, val double: Double) : Dto()
}

data class MyKey(val id: String)
data class MyClass(val id: String, val dto: Dto)
