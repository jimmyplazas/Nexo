package dev.alejo.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.auth.domain.EmailValidator
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.error_email_not_verified
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
                observeTextState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )

    val isEmailValid = snapshotFlow { state.value.emailTextFieldState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()

    private val isPasswordNotBlankFlow = snapshotFlow { state.value.passwordTextFieldState.text.toString() }
        .map { password -> password.isNotBlank() }
        .distinctUntilChanged()

    val isLoggingInFlow = state
        .map { it.isLoggingIn }
        .distinctUntilChanged()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
            else -> Unit
        }
    }

    private fun observeTextState() {
        combine(
            isEmailValid,
            isPasswordNotBlankFlow,
            isLoggingInFlow
        ) { isEmailValid, isPasswordNotBlank, isLoggingIn ->
            _state.update {
                it.copy(
                    canLogin = !isLoggingIn && isEmailValid && isPasswordNotBlank
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun login() {
        if (!state.value.canLogin) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(isLoggingIn = true)
            }

            val email = state.value.emailTextFieldState.text.toString()
            val password = state.value.passwordTextFieldState.text.toString()

            authService
                .login(
                    email = email,
                    password = password
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
                        DataError.Remote.FORBIDDEN -> UiText.Resource(Res.string.error_email_not_verified)
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