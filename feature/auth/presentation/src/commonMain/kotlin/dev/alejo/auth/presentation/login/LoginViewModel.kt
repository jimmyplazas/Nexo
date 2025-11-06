package dev.alejo.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.error_invalid_credentials

class LoginViewModel(
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventsChannel = Channel<LoginEvent>()
    val events = eventsChannel.receiveAsFlow()


    private val _state = MutableStateFlow(LoginState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLoginClick -> login()
            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoggingIn = true)
            }
            authService
                .login(
                    email = state.value.emailTextFieldState.text.toString(),
                    password = state.value.passwordTextFieldState.text.toString()
                )
                .onSuccess {
                    _state.update {
                        it.copy(isLoggingIn = false)
                    }
                    eventsChannel.send(LoginEvent.Success)
                }
                .onFailure { error ->
                    val loginError = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_invalid_credentials)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isLoggingIn = false,
                            error = loginError
                        )
                    }
                }
        }
    }

}