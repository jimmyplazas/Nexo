package dev.alejo.chat.presentation.chat_list

sealed interface ChatListAction {
    data object OnUserAvatarClick : ChatListAction
    data object OnDismissUserMenu : ChatListAction
    data object OnLogoutClick : ChatListAction
    data object OnConfirmLogout : ChatListAction
    data object OnDismissLogoutDialog : ChatListAction
    data object OnCreateChatCLick : ChatListAction
    data object OnProfileSettingsCLick : ChatListAction
    data class OnSelectChat(val chatId: String?) : ChatListAction
}