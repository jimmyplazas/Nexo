package dev.alejo.chat.presentation.chat_list

import dev.alejo.chat.presentation.model.ChatUi

sealed interface ChatListAction {
    data object OnUserAvatarClick : ChatListAction
    data object OnDismissUserMenu : ChatListAction
    data object OnLogoutClick : ChatListAction
    data object OnConfirmLogout : ChatListAction
    data object OnDismissLogoutDialog : ChatListAction
    data object OnCreateChatCLick : ChatListAction
    data object OnProfileSettingsCLick : ChatListAction
    data class OnChatClick(val chat: ChatUi) : ChatListAction
}