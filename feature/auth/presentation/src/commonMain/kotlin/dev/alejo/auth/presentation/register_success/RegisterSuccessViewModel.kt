package dev.alejo.auth.presentation.register_success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegisterSuccessViewModel(
    private val authServices: AuthService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventsChannel = Channel<RegisterSuccessEvent>()
    val events = eventsChannel.receiveAsFlow()

    private val email = savedStateHandle.get<String>("email")
        ?: throw IllegalArgumentException("No email passed to register success screen")

    private val _state = MutableStateFlow(RegisterSuccessState(
        registeredEmail = email
    ))

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
            initialValue = RegisterSuccessState()
        )

    fun onAction(action: RegisterSuccessAction) {
        when (action) {
            is RegisterSuccessAction.OnResendVerificationEmailClick -> resendVerification()
            else -> Unit
        }
    }

    private fun resendVerification() {
        if (state.value.isResendingVerificationEmail) {
            return
        }

        viewModelScope.launch {
            _state.value = state.value.copy(
                isResendingVerificationEmail = true
            )
            authServices
                .resendVerificationEmail(email)
                .onSuccess {
                    _state.value = state.value.copy(
                        isResendingVerificationEmail = false
                    )
                    eventsChannel.send(RegisterSuccessEvent.ResendVerificationEmailSuccess)
                }
                .onFailure { error ->
                    _state.value = state.value.copy(
                        isResendingVerificationEmail = false,
                        resendVerificationError = error.toUiText()
                    )
                }
        }
    }
}
