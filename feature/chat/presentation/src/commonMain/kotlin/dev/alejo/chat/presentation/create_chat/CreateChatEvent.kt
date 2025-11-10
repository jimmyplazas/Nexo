package dev.alejo.chat.presentation.create_chat

import dev.alejo.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat) : CreateChatEvent
}