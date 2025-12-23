package dev.alejo.chat.presentation.chat_detail

import dev.alejo.core.presentation.util.UiText

sealed interface ChatDetailEvent {
    data object OnChatLeft : ChatDetailEvent
    data class OnError(val error: UiText) : ChatDetailEvent
    data object OnNewMessage : ChatDetailEvent
}