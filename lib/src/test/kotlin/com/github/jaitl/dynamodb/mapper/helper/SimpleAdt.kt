package com.github.jaitl.dynamodb.mapper.helper

sealed class SimpleAdt {
    data class AdtOne(val string: String) : SimpleAdt()
    data class AdtTwo(val long: Long) : SimpleAdt()

    sealed class SecondSimpleAdt : SimpleAdt() {
        data class AdtSecondOne(val double: Double) : SecondSimpleAdt()
    }
}

data class SimpleDataAdt(val adt: SimpleAdt)
