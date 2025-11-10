package dev.alejo.chat.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.presentation.util.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isAddingParticipant: Boolean = false,
    val isLoadingParticipants: Boolean = false,
    val canAddParticipants: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
)