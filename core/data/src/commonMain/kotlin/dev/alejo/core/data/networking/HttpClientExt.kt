package dev.alejo.core.data.networking

import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * Executes a network request safely and handles potential platform-specific exceptions.
 * This is an `expect` function, requiring a platform-specific `actual` implementation
 * (e.g., for Android, iOS, JVM). The `actual` implementation should wrap the network
 * call in a try-catch block to handle exceptions like network connectivity issues
 * (e.g., `IOException` on Android) and map them to the appropriate [DataError.Remote].
 *
 * @param T The type of the successful response data.
 * @param execute A suspend lambda that performs the Ktor network request and returns an [HttpResponse].
 * @param handleResponse A suspend lambda that processes the [HttpResponse] and converts it into a [Result].
 *                       This is typically where you'd check status codes and deserialize the body.
 * @return A [Result] which is either [Result.Success] containing the data of type [T] or
 *         [Result.Failure] containing a [DataError.Remote].
 */
expect suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote>

suspend inline fun <reified T> safeCall(
    noinline execute: suspend () -> HttpResponse
): Result<T, DataError.Remote> {
    return platformSafeCall(
        execute = execute
    ) { response ->
        responseToResult(response)
    }
}

/**
 * Converts an [HttpResponse] into a [Result] object.
 *
 * This function processes the HTTP status code of the response. If the status is in the 2xx range,
 * it attempts to deserialize the response body into the specified type [T]. If the status indicates
 * an error (e.g., 4xx or 5xx), it maps the status code to a corresponding [DataError.Remote] type.
 *
 * @param T The type to which the successful response body should be deserialized.
 * @param response The [HttpResponse] object received from the network call.
 * @return A [Result.Success] containing the deserialized body of type [T] on a successful (2xx) response.
 *         A [Result.Failure] containing a [DataError.Remote] on any other status code or if deserialization fails.
 *         Specifically, it returns [DataError.Remote.SERIALIZATION] if the body cannot be transformed to type [T].
 */
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Failure(DataError.Remote.SERIALIZATION)
            }
        }
        400 -> Result.Failure(DataError.Remote.BAD_REQUEST)
        401 -> Result.Failure(DataError.Remote.UNAUTHORIZED)
        403 -> Result.Failure(DataError.Remote.FORBIDDEN)
        404 -> Result.Failure(DataError.Remote.NOT_FOUND)
        408 -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
        413 -> Result.Failure(DataError.Remote.PAYLOAD_TOO_LARGE)
        429 -> Result.Failure(DataError.Remote.TOO_MANY_REQUESTS)
        500 -> Result.Failure(DataError.Remote.SERVER_ERROR)
        503 -> Result.Failure(DataError.Remote.SERVICE_UNAVAILABLE)
        else -> Result.Failure(DataError.Remote.UNKNOWN)
    }
}

/**
 * Constructs a full URL from a given route string.
 *
 * This function ensures that the provided route is a valid, complete URL by prepending
 * the base URL (`UrlConstants.BASE_URL_HTTP`) if it's not already present. It handles
 * cases where the route might or might not start with a forward slash.
 *
 * - If the route already contains the base URL, it is returned as is.
 * - If the route starts with a "/", the base URL is prepended.
 * - Otherwise, the base URL and a "/" are prepended to the route.
 *
 * @param route The partial or full route string.
 * @return A complete URL string.
 */
fun constructRoute(route: String): String {
    return when {
        route.contains(UrlConstants.BASE_URL_HTTP) -> route
        route.startsWith("/") -> "${UrlConstants.BASE_URL_HTTP}$route"
        else -> "${UrlConstants.BASE_URL_HTTP}/$route"
    }
}