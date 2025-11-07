package dev.alejo.auth.presentation.login

sealed interface LoginEvent {
    data object Success : LoginEvent
}