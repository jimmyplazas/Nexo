package dev.alejo.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.alejo.chat.data.chat.KtorChatParticipantService
import dev.alejo.chat.data.chat.KtorChatService
import dev.alejo.chat.database.DatabaseFactory
import dev.alejo.chat.domain.chat.ChatParticipantService
import dev.alejo.chat.domain.chat.ChatService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module

val chatDataModule = module {
    includes(platformChatDataModule)
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
}