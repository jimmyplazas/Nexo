package dev.alejo.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import dev.alejo.chat.domain.models.ConnectionState
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.presentation.util.UiText

data class ChatDetailState(
    val chatUi: ChatUi? = null,
    val isLoading: Boolean = false,
    val messages: List<MessageUi> = emptyList(),
    val error: UiText? = null,
    val messageTextFieldState: TextFieldState = TextFieldState(),
    val canSendMessage: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val endReached: Boolean = false,
    val isChatOptionsOpen: Boolean = false,
    val isNearBottom: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)

data class BannerState(
    val formattedDate: UiText? = null,
    val isVisible: Boolean = false
)