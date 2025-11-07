package dev.alejo.core.domain.validation

data class PasswordValidationState(
    val hasMinLength: Boolean,
    val hasDigit: Boolean,
    val hasUppercase: Boolean
) {
    val isValidPassword: Boolean
        get() = hasMinLength && hasDigit && hasUppercase
}
