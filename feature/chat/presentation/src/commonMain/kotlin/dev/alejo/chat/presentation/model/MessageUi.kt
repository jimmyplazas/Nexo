package dev.alejo.chat.presentation.model

import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.presentation.util.UiText

sealed class MessageUi(open val id: String) {
    data class LocalUserMessage(
        override val id: String,
        val content: String,
        val deliveryStatus: ChatMessageDeliveryStatus,
        val isMenuOpen: Boolean,
        val formattedSentTime: UiText
    ) : MessageUi(id = id)

    data class OtherUserMessage(
        override val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUi
    ) : MessageUi(id = id)

    data class DateSeparator(
        override val id: String,
        val date: UiText
    ) : MessageUi(id = id)
}