package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.models.Chat
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface ChatService {
    suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>

    suspend fun getChats(): Result<List<Chat>, DataError.Remote>

    suspend fun getChatById(chatId: String): Result<Chat, DataError.Remote>

    suspend fun leaveCHat(chatId: String): EmptyResult<DataError.Remote>

    suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote>
}