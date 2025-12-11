package dev.alejo.core.domain.util

/**
 * Represents a hierarchy of possible data-related errors that can occur within the application.
 * This sealed interface is the base for categorizing errors, distinguishing between
 * those originating from remote sources (like a network API) and those from local sources
 * (like the device's storage).
 *
 * It inherits from [Error] to be throwable.
 *
 * @see Remote for errors related to network operations.
 * @see Local for errors related to local data storage.
 */
sealed interface DataError : Error {
    enum class Remote : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }

    enum class Connection : DataError {
        NOT_CONNECTED,
        MESSAGE_SEND_FAILED
    }
}