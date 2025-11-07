package dev.alejo.core.data.networking

import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
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
 * Performs a safe HTTP POST request.
 *
 * This extension function simplifies making a POST request using Ktor's [HttpClient].
 * It wraps the call in [safeCall] to automatically handle network errors and HTTP status codes,
 * returning a [Result] object.
 *
 * @param Request The type of the request body.
 * @param Response The expected type of the successful response body. Must be a non-nullable type.
 * @param route The API endpoint/route to which the request will be sent (e.g., "/users").
 * @param queryParams A map of query parameters to be appended to the URL.
 * @param body The request body object, which will be serialized and sent.
 * @param builder An optional lambda to further configure the [HttpRequestBuilder] (e.g., adding headers).
 * @return A [Result] which is either [Result.Success] containing the deserialized [Response] data
 *         or [Result.Failure] containing a [DataError.Remote].
 */
suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): Result<Response, DataError.Remote> {
    return safeCall {
        post {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}

/**
 * Performs a GET request to a specified route.
 *
 * This function is a wrapper around Ktor's `get` call, integrating it with the `safeCall`
 * mechanism to provide robust error handling and response parsing. It constructs the full URL,
 * adds any specified query parameters, and allows for further customization of the request
 * via the `builder` lambda. The response is automatically handled and wrapped in a [Result] type.
 *
 * @param Response The expected type of the successful response body, which must be `Any`.
 * @param route The API endpoint or route for the request (e.g., "/users/1"). The base URL will be prepended automatically.
 * @param queryParams A map of query parameters to be appended to the URL. Defaults to an empty map.
 * @param builder A lambda for advanced configuration of the [HttpRequestBuilder] (e.g., setting headers).
 * @return A [Result] which is either [Result.Success] containing the deserialized [Response] data
 *         or [Result.Failure] containing a [DataError.Remote] if the request fails or returns an error status code.
 */
suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): Result<Response, DataError.Remote> {
    return safeCall {
        get {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

/**
 * Sends a DELETE request to the specified [route] and wraps the result in a [Result] object.
 * This function handles network errors and HTTP status codes, mapping them to a [DataError.Remote].
 *
 * @param Response The expected type of the response body.
 * @param route The API endpoint path (e.g., "/users/123"). The base URL will be prepended automatically.
 * @param queryParams A map of query parameters to be appended to the URL.
 * @param builder A lambda for further configuring the [HttpRequestBuilder], such as adding headers.
 * @return A [Result] which is either [Result.Success] containing the deserialized [Response] data,
 *         or [Result.Failure] containing a [DataError.Remote].
 */
suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): Result<Response, DataError.Remote> {
    return safeCall {
        delete {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

/**
 * Performs a safe HTTP PUT request.
 *
 * This extension function simplifies making a PUT request by handling URL construction,
 * query parameters, body serialization, and wrapping the call in a `safeCall` to
 * manage exceptions and response status codes gracefully.
 *
 * @param Request The type of the request body object, which will be serialized.
 * @param Response The type of the expected successful response body. Must be a non-nullable type.
 * @param route The API endpoint or route for the request (e.g., "/users/1").
 * @param queryParams A map of key-value pairs to be added as URL query parameters.
 * @param body The request body object to be sent with the request.
 * @param builder An optional lambda to further customize the Ktor `HttpRequestBuilder`,
 *                allowing for setting headers, etc.
 * @return A [Result] which is either [Result.Success] containing the deserialized [Response] data,
 *         or [Result.Failure] containing a [DataError.Remote].
 */
suspend inline fun <reified Request, reified Response : Any> HttpClient.put(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): Result<Response, DataError.Remote> {
    return safeCall {
        put {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
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
        409 -> Result.Failure(DataError.Remote.CONFLICT)
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