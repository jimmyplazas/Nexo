package dev.alejo.chat.data.di

import dev.alejo.chat.data.chat.KtorChatParticipantService
import dev.alejo.chat.data.chat.KtorChatService
import dev.alejo.chat.domain.chat.ChatParticipantService
import dev.alejo.chat.domain.chat.ChatService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
}