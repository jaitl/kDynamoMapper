package com.github.jaitl.dynamodb

import java.time.Instant

sealed class Adt {
    data class AdtOne(val int: Int, val string: String) : Adt()
    data class AdtTwo(val long: Long, val instant: Instant, val double: Double) : Adt()
}

data class MyKey(val id: String)
data class MyAdtData(val id: String, val adt: Adt)
