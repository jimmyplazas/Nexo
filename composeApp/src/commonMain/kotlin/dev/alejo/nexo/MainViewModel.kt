package dev.alejo.nexo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.core.domain.auth.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val onSessionExpiredChannel = Channel<MainEvent>()
    val events = onSessionExpiredChannel.receiveAsFlow()

    private var previousRefreshToken: String? = null

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
                    onSessionExpiredChannel.send(MainEvent.OnSessionExpired)
                }

                previousRefreshToken = currentRefreshToken
            }
            .launchIn(viewModelScope)
    }

}