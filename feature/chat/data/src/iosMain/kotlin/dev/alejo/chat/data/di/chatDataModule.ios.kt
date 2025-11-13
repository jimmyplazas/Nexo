package dev.alejo.chat.data.di

import dev.alejo.chat.database.DatabaseFactory
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }
}