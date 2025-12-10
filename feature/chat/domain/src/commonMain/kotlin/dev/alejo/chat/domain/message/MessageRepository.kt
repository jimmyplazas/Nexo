package dev.alejo.chat.domain.message

import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.util.DataError

interface MessageRepository {

    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ) : EmptyResult<DataError.Local>
}