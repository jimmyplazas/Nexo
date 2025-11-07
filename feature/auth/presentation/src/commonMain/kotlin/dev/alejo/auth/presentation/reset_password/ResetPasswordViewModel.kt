package dev.alejo.auth.presentation.reset_password

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import dev.alejo.core.domain.validation.PasswordValidator
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.error_reset_password_token_invalid
import nexo.feature.auth.presentation.generated.resources.error_same_password

class ResetPasswordViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val token = savedStateHandle.get<String>("token")
        ?: throw IllegalStateException("No password reset token")

    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ResetPasswordState()
        )

    private val isNewPasswordValid = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { newPassword -> PasswordValidator.validate(newPassword).isValidPassword }
        .distinctUntilChanged()

    private fun observeValidationState() {
        isNewPasswordValid.onEach { isValid ->
            _state.update {
                it.copy(
                    canSubmit = isValid
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            ResetPasswordAction.OnSubmitClick -> resetPassword()
            ResetPasswordAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun resetPassword() {
        if (!state.value.canSubmit || state.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val newPassword = state.value.passwordTextState.text.toString()

            authService
                .resetPassword(token = token, newPassword = newPassword)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isResetSuccessful = true,
                            errorText = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        val errorMessage = when (error) {
                            DataError.Remote.UNAUTHORIZED -> UiText
                                .Resource(Res.string.error_reset_password_token_invalid)
                            DataError.Remote.CONFLICT -> UiText
                                .Resource(Res.string.error_same_password)
                            else -> error.toUiText()
                        }
                        it.copy(
                            isLoading = false,
                            errorText = errorMessage
                        )
                    }
                }
        }

    }

}