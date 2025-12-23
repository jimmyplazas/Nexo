package dev.alejo.chat.data.message

import dev.alejo.chat.data.dto.websocket.OutgoingWebSocketDto
import dev.alejo.chat.data.dto.websocket.WebSocketMessageDto
import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.data.mappers.toEntity
import dev.alejo.chat.data.mappers.toWebSocketDto
import dev.alejo.chat.data.network.KtorWebSocketConnector
import dev.alejo.chat.database.NexoChatDatabase
import dev.alejo.chat.domain.message.ChatMessageService
import dev.alejo.chat.domain.message.MessageRepository
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.domain.models.MessageWithSender
import dev.alejo.chat.domain.models.OutgoingNewMessage
import dev.alejo.core.data.database.safeDatabaseUpdate
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: NexoChatDatabase,
    private val chatMessageService: ChatMessageService,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val webSocketConnector: KtorWebSocketConnector,
    private val applicationScope: CoroutineScope
) : MessageRepository {

    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                status = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessages(chatId, before)
            .onSuccess { messages ->
                return safeDatabaseUpdate {
                    val entities = messages.map { it.toEntity() }
                    database.chatMessageDao.upsertMessagesAndSyncIfNecessary(
                        chatId = chatId,
                        serverMessages = entities,
                        pageSize = ChatMessageConstant.PAGE_SIZE,
                        shouldSync = before == null // Only sync for most recent page
                    )
                    messages
                }
            }
    }

    override fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>> {
        return database
            .chatMessageDao
            .getMessagesByChatId(chatId)
            .map { messages ->
                messages.map { it.toDomain() }
            }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return chatMessageService
            .deleteMessage(messageId)
            .onSuccess {
                applicationScope.launch {
                    database.chatMessageDao.deleteMessagesById(messageId)
                }.join()
            }
    }

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val dto = message.toWebSocketDto()

            val localUser = sessionStorage.observeAuthInf().first()?.user
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            val entity = dto.toEntity(
                senderId = localUser.id,
                deliveryStatus = ChatMessageDeliveryStatus.SENDING
            )

            database.chatMessageDao.upsertMessage(entity)

            return webSocketConnector
                .sendMessage(dto.toJsonPayload())
                .onFailure { error ->
                    applicationScope.launch {
                        database.chatMessageDao.updateDeliveryStatus(
                            messageId = entity.messageId,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
                            status = ChatMessageDeliveryStatus.FAILED.name
                        )
                    }.join()
                }
        }
    }

    override suspend fun retryMessage(messageId: String): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val message = database.chatMessageDao.getMessageById(messageId)
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                status = ChatMessageDeliveryStatus.SENDING.name
            )

            val outgoingNewMessage = OutgoingWebSocketDto.NewMessage(
                chatId = message.chatId,
                messageId = messageId,
                content = message.content
            )

            return webSocketConnector
                .sendMessage(outgoingNewMessage.toJsonPayload())
                .onFailure {
                    applicationScope.launch {
                        database.chatMessageDao.updateDeliveryStatus(
                            messageId = messageId,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
                            status = ChatMessageDeliveryStatus.FAILED.name
                        )
                    }.join()
                }
        }
    }

    private fun OutgoingWebSocketDto.NewMessage.toJsonPayload(): String {
        val webSocketMessage = WebSocketMessageDto(
            type = this.type.name,
            payload = json.encodeToString(this)
        )

        return json.encodeToString(webSocketMessage)
    }

}