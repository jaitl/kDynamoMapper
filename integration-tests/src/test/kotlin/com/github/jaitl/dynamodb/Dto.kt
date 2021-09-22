package com.github.jaitl.dynamodb

import java.time.Instant

sealed class Dto
data class DtoOne(val int: Int, val string: String)
data class DtoTwo(val long: Long, val instant: Instant, val double: Double)

data class MyKey(val id: String)
data class MyClassOne(val id: String, val dto: DtoOne)
data class MyClassTwo(val id: String, val dto: DtoTwo)