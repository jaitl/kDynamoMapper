package com.github.jaitl.dynamodb.mapper.converter.type

class MyTestNumber : Number() {
    override fun toByte(): Byte = throw Exception()
    override fun toChar(): Char = throw Exception()
    override fun toDouble(): Double = throw Exception()
    override fun toFloat(): Float = throw Exception()
    override fun toInt(): Int = throw Exception()
    override fun toLong(): Long = throw Exception()
    override fun toShort(): Short = throw Exception()
}
