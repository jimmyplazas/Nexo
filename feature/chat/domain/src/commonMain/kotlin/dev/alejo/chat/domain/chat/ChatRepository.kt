package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.models.Chat
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>
}