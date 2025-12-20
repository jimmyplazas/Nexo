package dev.alejo.chat.data.notification

import dev.alejo.chat.data.dto.request.RegisterDeviceTokenRequest
import dev.alejo.chat.domain.notification.DeviceTokenService
import dev.alejo.core.data.networking.delete
import dev.alejo.core.data.networking.post
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorDeviceTokenService(
    private val httpClient: HttpClient
) : DeviceTokenService {

    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/register",
            body = RegisterDeviceTokenRequest(
                token = token,
                platform = platform
            )
        )
    }

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/notification/$token"
        )
    }

}