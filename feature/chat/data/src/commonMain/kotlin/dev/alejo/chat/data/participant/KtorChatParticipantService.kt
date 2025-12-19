package dev.alejo.chat.data.participant

import dev.alejo.chat.data.dto.ChatParticipantDto
import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.domain.participant.ChatParticipantService
import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.core.data.networking.get
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.map
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorChatParticipantService(
    private val httpClient: HttpClient
) : ChatParticipantService {
    override suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        ).map { it.toDomain() }
    }

    override suspend fun getLocalParticipant(): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants"
        ).map { it.toDomain() }
    }
}