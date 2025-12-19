package dev.alejo.chat.domain.participant

import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.util.DataError

interface ChatParticipantRepository {
    suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError>
}