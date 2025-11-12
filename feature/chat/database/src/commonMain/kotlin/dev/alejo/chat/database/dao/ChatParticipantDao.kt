package dev.alejo.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.alejo.chat.database.entities.ChatParticipantEntity

@Dao
interface ChatParticipantDao {

    @Upsert
    suspend fun upsertChatParticipant(participant: ChatParticipantEntity)

    @Upsert
    suspend fun upsertChatParticipants(participants: List<ChatParticipantEntity>)

    @Query("SELECT * FROM chatparticipantentity")
    suspend fun getAllParticipants(): List<ChatParticipantEntity>

}