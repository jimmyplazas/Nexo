package dev.alejo.chat.presentation.chat_detail

import dev.alejo.chat.presentation.model.MessageUi

sealed interface ChatDetailAction {
    data object OnSendMessageClick : ChatDetailAction
    data object OnScrollToTop : ChatDetailAction
    data class OnSelectChat(val chatId: String?) : ChatDetailAction
    data class OnDeleteMessageClick(val message: MessageUi.LocalUserMessage?) : ChatDetailAction
    data class OnMessageLongClick(val message: MessageUi.LocalUserMessage?) : ChatDetailAction
    data class OnRetryClick(val message: MessageUi.LocalUserMessage?) : ChatDetailAction
    data object OnDismissMessageMenu : ChatDetailAction
    data object OnBackCLick : ChatDetailAction
    data object OnChatOptionsCLick : ChatDetailAction
    data object OnChatMembersCLick : ChatDetailAction
    data object OnLeaveChatCLick : ChatDetailAction
    data object OnDismissChatOptions : ChatDetailAction
}