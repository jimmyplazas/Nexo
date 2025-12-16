package dev.alejo.chat.presentation.mappers

import dev.alejo.chat.domain.models.MessageWithSender
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.chat.presentation.util.DateUtils

fun MessageWithSender.toUi(localUserId: String): MessageUi {
    val isFromCLocalUser = this.sender.userId == localUserId
    return if (isFromCLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt)
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = this.sender.toUi()
        )
    }
}