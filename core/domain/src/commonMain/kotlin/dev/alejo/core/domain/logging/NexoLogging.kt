package dev.alejo.core.domain.logging

/**
 * A generic logging interface to abstract logging implementations.
 * This allows for a consistent logging API across different modules
 * and facilitates easy swapping of the underlying logging framework.
 */
interface NexoLogger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}