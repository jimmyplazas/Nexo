package dev.alejo.core.data.networking

import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import platform.Foundation.NSURLErrorCallIsActive
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDataNotAllowed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorInternationalRoamingOff
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorResourceUnavailable
import platform.Foundation.NSURLErrorTimedOut
import kotlin.coroutines.coroutineContext

/**
 * A platform-specific (iOS) wrapper for making safe network calls using Ktor's Darwin engine.
 * It executes an HTTP request and handles platform-specific exceptions, mapping them to
 * a standardized [DataError.Remote].
 *
 * This function catches common Ktor and Darwin-specific exceptions, such as those related to
 * network connectivity, timeouts, and serialization, and translates them into a [Result.Failure]
 * with a corresponding [DataError.Remote] type. This abstracts away the platform-specific
 * error handling from the call site.
 *
 * @param T The type of the successful data returned in the [Result].
 * @param execute A suspend lambda that performs the Ktor HTTP request and returns an [HttpResponse].
 * @param handleResponse A suspend lambda that processes the [HttpResponse] and transforms it into a [Result].
 *                       This is only called if the initial request does not throw an exception.
 * @return A [Result] which is either a [Result.Success] containing the data of type [T] or a
 *         [Result.Failure] containing a [DataError.Remote].
 */
actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: DarwinHttpRequestException) {
        handleDarwinException(e)
    } catch (e: UnresolvedAddressException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: HttpRequestTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        Result.Failure(DataError.Remote.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}

/**
 * Handles Darwin-specific network exceptions by mapping them to standardized [DataError.Remote] types.
 *
 * This function inspects the underlying `NSError` from a [DarwinHttpRequestException] to identify
 * the root cause of the network failure. It specifically checks for `NSURLErrorDomain` errors
 * and categorizes them into more specific error types like no internet connection or request timeout.
 *
 * @param e The [DarwinHttpRequestException] caught during the network call.
 * @return A [Result.Failure] containing the appropriate [DataError.Remote] subtype.
 */
private fun handleDarwinException(
    e: DarwinHttpRequestException
): Result<Nothing, DataError.Remote> {
    val nsError = e.origin

    return if(nsError.domain == NSURLErrorDomain) {
        when(nsError.code) {
            NSURLErrorNotConnectedToInternet,
            NSURLErrorNetworkConnectionLost,
            NSURLErrorCannotFindHost,
            NSURLErrorDNSLookupFailed,
            NSURLErrorResourceUnavailable,
            NSURLErrorInternationalRoamingOff,
            NSURLErrorCallIsActive,
            NSURLErrorDataNotAllowed -> {
                Result.Failure(DataError.Remote.NO_INTERNET)
            }
            NSURLErrorTimedOut -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
            else -> Result.Failure(DataError.Remote.UNKNOWN)
        }
    } else {
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}