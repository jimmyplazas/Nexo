package dev.alejo.core.domain.auth

import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError

interface AuthService {
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote>
    suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote>
    suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote>
    suspend fun login(email: String, password: String): EmptyResult<DataError.Remote>
}