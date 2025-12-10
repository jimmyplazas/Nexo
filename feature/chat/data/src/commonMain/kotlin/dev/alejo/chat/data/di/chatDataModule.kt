package dev.alejo.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.alejo.chat.data.chat.KtorChatParticipantService
import dev.alejo.chat.data.chat.KtorChatService
import dev.alejo.chat.data.chat.OfflineFirstChatRepository
import dev.alejo.chat.data.chat.WebSocketChatConnectionClient
import dev.alejo.chat.data.message.OfflineFirstMessageRepository
import dev.alejo.chat.data.network.KtorWebSocketConnector
import dev.alejo.chat.database.DatabaseFactory
import dev.alejo.chat.domain.chat.ChatConnectionClient
import dev.alejo.chat.domain.chat.ChatParticipantService
import dev.alejo.chat.domain.chat.ChatRepository
import dev.alejo.chat.domain.chat.ChatService
import dev.alejo.chat.domain.message.MessageRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module

val chatDataModule = module {
    includes(platformChatDataModule)
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(:: OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(:: OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(:: WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(:: KtorWebSocketConnector)
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}