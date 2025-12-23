package dev.alejo.core.domain.util

class DataErrorException(
    val error: DataError
) : Exception()