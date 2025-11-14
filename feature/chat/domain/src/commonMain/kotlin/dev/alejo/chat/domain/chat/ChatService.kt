package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.models.Chat
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface ChatService {
    suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>

    suspend fun getChats(): Result<List<Chat>, DataError.Remote>
}