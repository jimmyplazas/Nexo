package dev.alejo.chat.presentation.di

import dev.alejo.chat.presentation.chat_detail.ChatDetailViewModel
import dev.alejo.chat.presentation.chat_list.ChatListViewModel
import dev.alejo.chat.presentation.chat_list_detail.ChatListDetailViewModel
import dev.alejo.chat.presentation.create_chat.CreateChatViewModel
import dev.alejo.chat.presentation.manage_chat.ManageChatViewModel
import dev.alejo.chat.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ProfileViewModel)
}