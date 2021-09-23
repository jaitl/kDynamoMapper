package com.github.jaitl.dynamodb.mapper.helper

sealed class SimpleDto

data class DtoOne(val string: String) : SimpleDto()
data class DtoTwo(val long: Long) : SimpleDto()

data class SimpleDataDto(val dto: SimpleDto)
