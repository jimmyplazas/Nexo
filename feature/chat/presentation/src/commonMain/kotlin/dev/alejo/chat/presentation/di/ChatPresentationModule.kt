package dev.alejo.chat.presentation.di

import dev.alejo.chat.presentation.chat_list.ChatListViewModel
import dev.alejo.chat.presentation.chat_list_detail.ChatListDetailViewModel
import dev.alejo.chat.presentation.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
}