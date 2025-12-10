package dev.alejo.chat.data.message

import dev.alejo.chat.database.NexoChatDatabase
import dev.alejo.chat.domain.message.MessageRepository
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.core.data.database.safeDatabaseUpdate
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: NexoChatDatabase
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

}