@file:OptIn(FlowPreview::class)

package dev.alejo.chat.data.network

import dev.alejo.chat.data.dto.websocket.WebSocketMessageDto
import dev.alejo.chat.data.lifecycle.AppLifecycleObserver
import dev.alejo.chat.domain.models.ConnectionState
import dev.alejo.core.data.networking.UrlConstants
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.logging.NexoLogger
import dev.alejo.core.domain.util.DataError
import dev.alejo.feature.chat.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class KtorWebSocketConnector(
    private val httpClient: HttpClient,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val connectionErrorHandler: ConnectionErrorHandler,
    private val connectionRetryHandler: ConnectionRetryHandler,
    private val appLifecycleObserver: AppLifecycleObserver,
    private val connectivityObserver: ConnectivityObserver,
    private val logger: NexoLogger
) {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private var currentSession: WebSocketSession? = null

    private val isConnected = connectivityObserver
        .isConnected
        .debounce(1.seconds)
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    private val isInForeground = appLifecycleObserver
        .isInForeground
        .onEach { isInForeground ->
            if (isInForeground) {
                connectionRetryHandler.resetDelay()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = combine(
        sessionStorage.observeAuthInf(),
        isConnected,
        isInForeground
    ) { authInf, isConnected, isInForeground ->
        when {
            authInf == null -> {
                logger.info("No authentication details. Clearing session and disconnecting.")
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
                connectionRetryHandler.resetDelay()
                null
            }

            !isInForeground -> {
                logger.info("App in background, disconnecting socket proactively.")
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
                null
            }

            !isConnected -> {
                logger.info("Device is disconnected, closing web socket connection.")
                _connectionState.value = ConnectionState.ERROR_NETWORK
                currentSession?.close()
                currentSession = null
                null
            }

            else -> {
                logger.info("App in foreground and connected, establishing connection...")

                if (_connectionState.value !in listOf(
                        ConnectionState.CONNECTING,
                        ConnectionState.CONNECTED
                    )
                ) {
                    _connectionState.value = ConnectionState.CONNECTING
                }

                authInf
            }
        }
    }.flatMapLatest { authInfo ->
        if (authInfo == null) {
            emptyFlow()
        } else {
            createWebSocketFlow(authInfo.accessToken)
                // Catch block to transform exceptions for platform compatibility
                .catch { e ->
                    logger.error("Exception in WebSocket", e)

                    currentSession?.close()
                    currentSession = null

                    val transformedException = connectionErrorHandler.transformException(e)
                    throw transformedException
                }
                .retryWhen { t, attempt ->
                    logger.info("Connectionfailed on attempt $attempt")

                    val shouldRetry = connectionRetryHandler.shouldRetry(t, attempt)

                    if (shouldRetry) {
                        _connectionState.value = ConnectionState.CONNECTING
                        connectionRetryHandler.applyRetryDelay(attempt)
                    }

                    shouldRetry
                }
                // Catch block for non-retriable errors
                .catch { e ->
                    logger.error("Unhandled WebSocket error", e)

                    _connectionState.value = connectionErrorHandler.getConnectionStateForError(e)
                }
        }
    }

    private fun createWebSocketFlow(accessToken: String) = callbackFlow {
        _connectionState.value = ConnectionState.CONNECTING

        currentSession = httpClient.webSocketSession(
            urlString = "${UrlConstants.BASE_URL_WS}/chat"
        ) {
            header("Authorization", "Bearer $accessToken")
            header("X-API-Key", BuildKonfig.API_KEY)
        }

        currentSession?.let { session ->
            _connectionState.value = ConnectionState.CONNECTED

            session
                .incoming
                .consumeAsFlow()
                .buffer(
                    capacity = 100
                )
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            logger.info("Received raw text frame: $text")

                            val messageDto = json.decodeFromString<WebSocketMessageDto>(text)
                            send(messageDto)
                        }

                        is Frame.Ping -> {
                            logger.debug("Received ping server, sending pong")
                            session.send(Frame.Pong(frame.data))
                        }

                        else -> Unit
                    }
                }
        } ?: throw Exception("Failed to establish Web Socket connection")

        awaitClose {
            launch(NonCancellable) {
                logger.info("Disconnecting from Web Socket connection...")
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
            }

        }
    }

    suspend fun sendMessage(message: String): EmptyResult<DataError.Connection> {
        val connectionState = connectionState.value

        if (currentSession == null || connectionState != ConnectionState.CONNECTED) {
            return Result.Failure(DataError.Connection.NOT_CONNECTED)
        }

        return try {
            currentSession?.send(message)
            Result.Success(Unit)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            logger.error("Unable to send WebSocket message", e)
            Result.Failure(DataError.Connection.MESSAGE_SEND_FAILED)
        }
    }
}