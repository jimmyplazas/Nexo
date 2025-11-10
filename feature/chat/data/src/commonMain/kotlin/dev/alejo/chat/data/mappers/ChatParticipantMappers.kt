package dev.alejo.chat.data.mappers

import dev.alejo.chat.data.dto.ChatParticipantDto
import dev.alejo.chat.domain.models.ChatParticipant

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}