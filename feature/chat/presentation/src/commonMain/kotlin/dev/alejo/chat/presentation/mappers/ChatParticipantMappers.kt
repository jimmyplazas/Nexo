package dev.alejo.chat.presentation.mappers

import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.domain.auth.User

fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )
}

fun User.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = id,
        username = username,
        initials = username.take(2).uppercase(),
        imageUrl = profilePicture
    )
}