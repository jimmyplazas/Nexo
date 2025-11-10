package dev.alejo.chat.presentation.mappers

import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi

fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )
}