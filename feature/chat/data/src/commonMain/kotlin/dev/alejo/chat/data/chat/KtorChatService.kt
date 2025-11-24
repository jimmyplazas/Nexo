package dev.alejo.chat.data.chat

import dev.alejo.chat.data.dto.ChatDto
import dev.alejo.chat.data.dto.request.CreateChatRequest
import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.domain.chat.ChatService
import dev.alejo.chat.domain.models.Chat
import dev.alejo.core.data.networking.delete
import dev.alejo.core.data.networking.get
import dev.alejo.core.data.networking.post
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.asEmptyResult
import dev.alejo.core.domain.map
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorChatService(
    private val httpClient: HttpClient
) : ChatService {
    override suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote> {
        return httpClient.post<CreateChatRequest, ChatDto>(
            route = "/chat",
            body = CreateChatRequest(
                otherUserIds = otherUserIds
            )
        ).map { it.toDomain() }
    }

    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(
            route = "/chat"
        ).map { chatDtos ->
            chatDtos.map { it.toDomain() }
        }
    }

    override suspend fun getChatById(chatId: String): Result<Chat, DataError.Remote> {
        return httpClient.get<ChatDto>(
            route = "/chat/$chatId"
        ).map { it.toDomain() }
    }

    override suspend fun leaveCHat(chatId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/chat/$chatId/leave"
        ).asEmptyResult()
    }
}