package dev.alejo.chat.domain.message

import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface ChatMessageService {
    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError.Remote>

    suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote>
}