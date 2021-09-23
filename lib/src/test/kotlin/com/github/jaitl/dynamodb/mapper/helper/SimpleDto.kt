package com.github.jaitl.dynamodb.mapper.helper

sealed interface SimpleDto

data class DtoOne(val string: String) : SimpleDto
data class DtoTwo(val long: Long) : SimpleDto

sealed class SecondSimpleDto : SimpleDto
data class DtoSecondOne(val double: Double) : SecondSimpleDto()


data class SimpleDataDto(val dto: SimpleDto)
