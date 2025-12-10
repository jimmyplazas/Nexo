package dev.alejo.chat.data.dto.websocket

import kotlinx.serialization.Serializable

enum class IncomingWebSocketType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED
}

@Serializable
sealed class IncomingWebSocketDto(
    private val type: IncomingWebSocketType
) {

    @Serializable
    data class NewMessageDto(
        val id: String,
        val chatId: String,
        val content: String,
        val senderId: String,
        val createdAt: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.NEW_MESSAGE)

    data class MessageDeletedDto(
        val messageId: String,
        val chatId: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.MESSAGE_DELETED)

    @Serializable
    data class ProfilePictureUpdatedDto(
        val userId: String,
        val newUrl: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.PROFILE_PICTURE_UPDATED)

    @Serializable
    data class ChatParticipantsUpdatedDto(
        val chatId: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED)

}