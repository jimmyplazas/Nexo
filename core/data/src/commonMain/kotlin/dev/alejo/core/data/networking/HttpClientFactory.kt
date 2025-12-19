package dev.alejo.core.data.networking

import dev.alejo.core.data.BuildKonfig
import dev.alejo.core.data.dto.AuthInfoSerializable
import dev.alejo.core.data.dto.requests.RefreshRequest
import dev.alejo.core.data.mappers.toDomain
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.logging.NexoLogger
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

/**
 * Factory class responsible for creating and configuring Ktor `HttpClient` instances.
 *
 * This class encapsulates the setup of a common `HttpClient` with pre-configured plugins
 * such as JSON serialization, timeouts, logging, WebSockets, and default request headers.
 * By centralizing the client configuration, it ensures consistency across different
 * parts of the application that require making HTTP requests.
 *
 * @property nexoLogger A custom logger interface for logging HTTP requests and responses.
 */
class HttpClientFactory(
    private val nexoLogger: NexoLogger,
    private val sessionStorage: SessionStorage
) {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        nexoLogger.debug(message)
                    }
                }
                level = LogLevel.ALL
            }

            install(WebSockets) {
                pingIntervalMillis = 20_000L
            }

            defaultRequest {
                header("x-api-key", BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        sessionStorage
                            .observeAuthInf()
                            .firstOrNull()
                            ?.let {
                                BearerTokens(
                                    accessToken = it.accessToken,
                                    refreshToken = it.refreshToken
                                )
                            }
                    }
                    refreshTokens {
                        if (response.request.url.encodedPath.contains("auth/")) {
                            return@refreshTokens null
                        }

                        val authInfo = sessionStorage.observeAuthInf().firstOrNull()
                        if (authInfo?.refreshToken.isNullOrBlank()) {
                            sessionStorage.set(null)
                            return@refreshTokens null
                        }

                        var bearerTokens: BearerTokens? = null

                        client.post<RefreshRequest, AuthInfoSerializable>(
                            route = "/auth/refresh",
                            body = RefreshRequest(
                                refreshToken = authInfo.refreshToken
                            ),
                            builder = {
                                markAsRefreshTokenRequest()
                            }
                        ).onSuccess { newAuthInfo ->
                            sessionStorage.set(
                                newAuthInfo.toDomain(authInfo.user.profilePicture)
                            )
                            bearerTokens = BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        }.onFailure { error ->
                            sessionStorage.set(null)
                        }

                        bearerTokens
                    }
                }
            }
        }
    }
}