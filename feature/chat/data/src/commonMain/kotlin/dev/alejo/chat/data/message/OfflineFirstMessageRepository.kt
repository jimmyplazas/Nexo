package dev.alejo.chat.data.message

import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.data.mappers.toEntity
import dev.alejo.chat.database.NexoChatDatabase
import dev.alejo.chat.domain.message.ChatMessageService
import dev.alejo.chat.domain.message.MessageRepository
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.domain.models.MessageWithSender
import dev.alejo.core.data.database.safeDatabaseUpdate
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: NexoChatDatabase,
    private val chatMessageService: ChatMessageService
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

}