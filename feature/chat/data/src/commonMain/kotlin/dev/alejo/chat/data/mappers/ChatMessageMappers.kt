package dev.alejo.chat.data.mappers

import dev.alejo.chat.data.dto.ChatMessageDto
import dev.alejo.chat.database.entities.ChatMessageEntity
import dev.alejo.chat.database.view.LastMessageView
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.SENT
    )
}

fun LastMessageView.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(deliveryStatus)
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        chatId = chatId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        deliveryStatus = deliveryStatus.name,
    )
}

fun ChatMessage.toLastMessageView(): LastMessageView {
    return LastMessageView(
        messageId = id,
        chatId = chatId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        deliveryStatus = deliveryStatus.name,
    )
}

fun LastMessageView.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = messageId,
        chatId = chatId,
        content = content,
        timestamp = timestamp,
        senderId = senderId,
        deliveryStatus = deliveryStatus,
    )
}