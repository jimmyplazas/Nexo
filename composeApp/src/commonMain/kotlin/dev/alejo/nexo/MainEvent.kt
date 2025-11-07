package dev.alejo.nexo

sealed interface MainEvent {
    data object OnSessionExpired : MainEvent
}