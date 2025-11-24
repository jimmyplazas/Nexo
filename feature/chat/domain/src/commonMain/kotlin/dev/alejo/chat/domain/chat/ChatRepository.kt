package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.models.Chat
import dev.alejo.chat.domain.models.ChatInfo
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getChatInfoById(chatId: String): Flow<ChatInfo>
    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>
    suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote>
    suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote>
    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>
}