package dev.alejo.core.data.networking

import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

/**
 * A platform-specific implementation for making safe network calls on Android.
 *
 * This function wraps an HTTP request execution and response handling logic within a try-catch block
 * to gracefully handle common networking and serialization exceptions. It maps specific
 * exceptions to a predefined set of [DataError.Remote] types, providing a consistent
 * error handling mechanism for the application's data layer.
 *
 * This is the `actual` implementation for the Android platform.
 *
 * It catches and handles the following exceptions:
 * - [UnknownHostException], [UnresolvedAddressException], [ConnectException]: Mapped to [DataError.Remote.NO_INTERNET].
 * - [SocketTimeoutException], [HttpRequestTimeoutException]: Mapped to [DataError.Remote.REQUEST_TIMEOUT].
 * - [SerializationException]: Mapped to [DataError.Remote.SERIALIZATION].
 * - Any other [Exception]: Mapped to [DataError.Remote.UNKNOWN], after ensuring the coroutine is still active.
 *
 * @param T The type of the successful data expected in the [Result].
 * @param execute A suspend lambda that performs the network request and returns an [HttpResponse].
 * @param handleResponse A suspend lambda that processes the [HttpResponse] and transforms it into a [Result].
 * @return A [Result] object, which is either a [Result.Success] containing the data of type [T]
 * or a [Result.Failure] containing a [DataError.Remote].
 */
actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: UnknownHostException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: UnresolvedAddressException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: ConnectException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: SocketTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: HttpRequestTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        Result.Failure(DataError.Remote.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}