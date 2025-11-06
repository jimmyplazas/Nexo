package dev.alejo.core.data.auth

import dev.alejo.core.data.dto.AuthInfoSerializable
import dev.alejo.core.data.dto.requests.EmailRequest
import dev.alejo.core.data.dto.requests.LoginRequest
import dev.alejo.core.data.dto.requests.RegisterRequest
import dev.alejo.core.data.mappers.toDomain
import dev.alejo.core.data.networking.get
import dev.alejo.core.data.networking.post
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.auth.AuthInfo
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.map
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post< LoginRequest, AuthInfoSerializable>(
            route = "/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password
            )
        )
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(email = email)
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/auth/verify",
            queryParams = mapOf("token" to token)
        )
    }

}