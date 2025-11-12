package dev.alejo.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.alejo.chat.database.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Upsert
    suspend fun upsertMessage(message: ChatMessageEntity)

    @Upsert
    suspend fun upsertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chatmessageentity WHERE messageId IN (:messageIds)")
    suspend fun deleteMessagesById(messageIds: List<String>)

    @Query("SELECT * FROM chatmessageentity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chatmessageentity WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): ChatMessageEntity?

}