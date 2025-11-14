package dev.alejo.chat.data.mappers

import dev.alejo.chat.data.dto.ChatDto
import dev.alejo.chat.database.entities.ChatEntity
import dev.alejo.chat.database.entities.ChatWithParticipants
import dev.alejo.chat.domain.models.Chat
import kotlin.time.Instant

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.parse(lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}

fun ChatWithParticipants.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        chatId = id,
        lastActivityAt = lastActivityAt.toEpochMilliseconds()
    )
}