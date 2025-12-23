package dev.alejo.chat.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NexoChatDatabaseConstructor : RoomDatabaseConstructor<NexoChatDatabase> {
    override fun initialize(): NexoChatDatabase
}