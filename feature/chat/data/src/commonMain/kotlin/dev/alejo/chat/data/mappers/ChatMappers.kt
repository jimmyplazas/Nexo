package dev.alejo.chat.data.mappers

import dev.alejo.chat.data.dto.ChatDto
import dev.alejo.chat.database.entities.ChatEntity
import dev.alejo.chat.database.entities.ChatInfoEntity
import dev.alejo.chat.database.entities.ChatWithParticipants
import dev.alejo.chat.database.entities.MessageWithSender
import dev.alejo.chat.domain.models.Chat
import dev.alejo.chat.domain.models.ChatInfo
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.domain.models.ChatParticipant
import kotlin.time.Instant

typealias DataMessageWithSender = MessageWithSender
typealias DomainMessageWithSender = dev.alejo.chat.domain.models.MessageWithSender

fun ChatDto.toDomain(): Chat {
    val lastMessageSenderUsername = lastMessage?.let { message ->
        participants.find { it.userId == message.senderId }?.username
    }
    return Chat(
        id = id,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.parse(lastActivityAt),
        lastMessage = lastMessage?.toDomain(),
        lastMessageSenderUsername = lastMessageSenderUsername
    )
}
fun ChatEntity.toDomain(
    participants: List<ChatParticipant>,
    lastMessage: dev.alejo.chat.domain.models.ChatMessage? = null
): Chat {
    val lastMessageSenderUsername = lastMessage?.let { message ->
        participants.find { it.userId == message.senderId }?.username
    }
    return Chat(
        id = chatId,
        participants = participants,
        lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
        lastMessage = lastMessage,
        lastMessageSenderUsername = lastMessageSenderUsername
    )
}

fun ChatWithParticipants.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessage?.toDomain(),
        lastMessageSenderUsername = lastMessage?.senderUsername
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        chatId = id,
        lastActivityAt = lastActivityAt.toEpochMilliseconds()
    )
}

fun DataMessageWithSender.toDomain(): DomainMessageWithSender {
    return DomainMessageWithSender(
        message = message.toDomain(),
        sender = sender.toDomain(),
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.message.deliveryStatus)
    )
}

fun ChatInfoEntity.toDomain(): ChatInfo {
    return ChatInfo(
        chat = chat.toDomain(
            participants = participants.map{ it.toDomain() }
        ),
        messages = messagesWithSenders.map { it.toDomain() }
    )
}