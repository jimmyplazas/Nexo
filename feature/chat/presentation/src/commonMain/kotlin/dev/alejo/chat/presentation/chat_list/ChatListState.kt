package dev.alejo.chat.presentation.chat_list

import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val isLoading: Boolean = false,
    val localParticipant: ChatParticipantUi? = null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val selectedChatId: String? = null
)