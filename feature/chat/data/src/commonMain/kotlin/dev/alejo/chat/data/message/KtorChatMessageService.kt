package dev.alejo.chat.data.message

import dev.alejo.chat.data.dto.ChatMessageDto
import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.domain.message.ChatMessageService
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.core.data.networking.delete
import dev.alejo.core.data.networking.get
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.map
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
) : ChatMessageService {

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstant.PAGE_SIZE
                if (before != null) {
                    this["before"] = before
                }
            }
        ).map { it.map { it.toDomain() } }
    }

    override suspend fun deleteMessage(
        messageId: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/messages/$messageId"
        )
    }
}