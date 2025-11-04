package dev.alejo.core.data.auth

import dev.alejo.core.data.dto.requests.RegisterRequest
import dev.alejo.core.data.networking.post
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {
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
}