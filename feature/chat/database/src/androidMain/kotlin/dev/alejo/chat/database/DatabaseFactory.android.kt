package dev.alejo.chat.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<NexoChatDatabase> {
        val dbFile = context.applicationContext.getDatabasePath(NexoChatDatabase.DB_NAME)

        return Room.databaseBuilder(
            context.applicationContext,
            dbFile.absolutePath
        )
    }
}