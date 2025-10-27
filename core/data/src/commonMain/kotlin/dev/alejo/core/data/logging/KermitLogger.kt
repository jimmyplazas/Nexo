package dev.alejo.core.data.logging

import co.touchlab.kermit.Logger
import dev.alejo.core.domain.logging.NexoLogger

/**
 * An implementation of [NexoLogger] that uses the Kermit logging library.
 * This object acts as a concrete wrapper around [co.touchlab.kermit.Logger],
 * mapping the standard logging levels (debug, info, warn, error) to their
 * corresponding Kermit functions.
 */
object KermitLogger : NexoLogger {
    override fun debug(message: String) {
        Logger.d(message)
    }

    override fun info(message: String) {
        Logger.i(message)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Logger.e(message, throwable)
    }
}