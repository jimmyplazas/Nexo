package dev.alejo.core.domain.auth

import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError

interface AuthService {
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote>
}