package dev.alejo.core.domain.auth

import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface AuthService {
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote>
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote>
    suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote>
    suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote>
    suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote>
    suspend fun resetPassword(token: String, newPassword: String): EmptyResult<DataError.Remote>
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ) : EmptyResult<DataError.Remote>
}