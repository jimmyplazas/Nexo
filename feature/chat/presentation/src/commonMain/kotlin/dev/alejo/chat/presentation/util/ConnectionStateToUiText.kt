package dev.alejo.chat.presentation.util

import dev.alejo.chat.domain.models.ConnectionState
import dev.alejo.core.presentation.util.UiText
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.network_error
import nexo.feature.chat.presentation.generated.resources.offline
import nexo.feature.chat.presentation.generated.resources.online
import nexo.feature.chat.presentation.generated.resources.reconnecting
import nexo.feature.chat.presentation.generated.resources.unknown_error

fun ConnectionState.toUiText(): UiText {
    val resource = when (this) {
        ConnectionState.DISCONNECTED -> Res.string.offline
        ConnectionState.CONNECTING -> Res.string.reconnecting
        ConnectionState.CONNECTED -> Res.string.online
        ConnectionState.ERROR_NETWORK -> Res.string.network_error
        ConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }
    return UiText.Resource(resource)
}