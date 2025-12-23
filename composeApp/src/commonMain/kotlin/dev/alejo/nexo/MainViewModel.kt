package dev.alejo.nexo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.chat.domain.notification.DeviceTokenService
import dev.alejo.chat.domain.notification.PushNotificationService
import dev.alejo.core.data.util.PlatformUtils
import dev.alejo.core.domain.auth.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage,
    private val pushNotificationService: PushNotificationService,
    private val deviceTokenService: DeviceTokenService
) : ViewModel() {

    private val onSessionExpiredChannel = Channel<MainEvent>()
    val events = onSessionExpiredChannel.receiveAsFlow()

    private var previousRefreshToken: String? = null
    private var currentDeviceToken: String? = null
    private var previousDeviceToken: String? = null

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(MainState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeSession()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainState()
        )

    init {
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInf().firstOrNull()
            _state.value = _state.value.copy(
                isCheckingAuth = false,
                isLoggedIn = authInfo != null
            )
        }
    }

    private fun observeSession() {
        sessionStorage
            .observeAuthInf()
            .onEach { authInfo ->
                val currentRefreshToken = authInfo?.refreshToken
                val isSessionExpired = previousRefreshToken != null && currentRefreshToken == null
                if (isSessionExpired) {
                    sessionStorage.set(null)
                    _state.update {
                        it.copy(isLoggedIn = false)
                    }
                    currentDeviceToken?.let { token ->
                        unregisterDeviceToken(token)
                    }
                    onSessionExpiredChannel.send(MainEvent.OnSessionExpired)
                }

                previousRefreshToken = currentRefreshToken
            }
            .combine(
                pushNotificationService.observeDeviceToken()
            ) { authInfo, deviceToken ->
                currentDeviceToken = deviceToken

                if (deviceToken != null && deviceToken != previousDeviceToken && authInfo != null) {
                        registerDeviceToken(deviceToken, PlatformUtils.getOSName())
                }
            }
            .launchIn(viewModelScope)
    }

    private fun registerDeviceToken(token: String, platform: String) {
        viewModelScope.launch {
            deviceTokenService.registerToken(token, platform)
        }
    }

    private fun unregisterDeviceToken(token: String) {
        viewModelScope.launch {
            deviceTokenService.unregisterToken(token)
        }
    }

}