package dev.alejo.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import dev.alejo.chat.database.dao.ChatDao
import dev.alejo.chat.database.dao.ChatMessageDao
import dev.alejo.chat.database.dao.ChatParticipantDao
import dev.alejo.chat.database.dao.ChatParticipantsCrossRefDao
import dev.alejo.chat.database.entities.ChatEntity
import dev.alejo.chat.database.entities.ChatMessageEntity
import dev.alejo.chat.database.entities.ChatParticipantCrossRef
import dev.alejo.chat.database.entities.ChatParticipantEntity
import dev.alejo.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class
    ],
    views = [
        LastMessageView::class
    ],
    version = 1
)
@ConstructedBy(NexoChatDatabaseConstructor::class)
abstract class NexoChatDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object {
        const val DB_NAME = "nexo.db"
    }
}