package dev.alejo.chat.domain.notification

import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError

interface DeviceTokenService {

    suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote>

    suspend fun unregisterToken(
        token: String
    ): EmptyResult<DataError.Remote>
}