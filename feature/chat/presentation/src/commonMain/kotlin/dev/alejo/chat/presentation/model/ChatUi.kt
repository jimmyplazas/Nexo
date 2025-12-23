package dev.alejo.chat.presentation.model

import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi

data class ChatUi(
    val id: String,
    val localParticipant: ChatParticipantUi,
    val otherParticipants: List<ChatParticipantUi>,
    val lastMessage: ChatMessage? = null,
    val lastMessageSenderUsername: String? = null
)
