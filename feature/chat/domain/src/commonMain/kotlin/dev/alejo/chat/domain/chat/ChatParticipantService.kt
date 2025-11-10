package dev.alejo.chat.domain.chat

import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface ChatParticipantService {
    suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote>
}