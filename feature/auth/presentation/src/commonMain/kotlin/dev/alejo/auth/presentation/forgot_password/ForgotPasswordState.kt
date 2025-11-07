package dev.alejo.auth.presentation.forgot_password

import androidx.compose.foundation.text.input.TextFieldState
import dev.alejo.core.presentation.util.UiText

data class ForgotPasswordState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val canSubmit: Boolean = false,
    val error: UiText? = null,
    val isEmailSentSuccessfully: Boolean = false
)