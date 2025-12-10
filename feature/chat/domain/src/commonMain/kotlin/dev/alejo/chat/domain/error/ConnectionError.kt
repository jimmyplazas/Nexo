package dev.alejo.chat.domain.error

import dev.alejo.core.domain.util.Error

enum class ConnectionError : Error {
    NOT_CONNECTED,
    MESSAGE_SEND_FAILED
}