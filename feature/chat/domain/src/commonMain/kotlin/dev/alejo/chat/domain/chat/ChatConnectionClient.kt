package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.error.ConnectionError
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.chat.domain.models.ConnectionState
import dev.alejo.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatConnectionClient {
    val chatMessages: Flow<ChatMessage>
    val connectionState: StateFlow<ConnectionState>
    suspend fun sendChatMessage(message: ChatMessage): EmptyResult<ConnectionError>
}