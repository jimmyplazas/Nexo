package dev.alejo.chat.presentation.manage_chat

import androidx.lifecycle.ViewModel
import dev.alejo.chat.presentation.components.manage_chat.ManageChatAction
import dev.alejo.chat.presentation.components.manage_chat.ManageChatState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ManageChatViewModel : ViewModel() {

    private val _events = Channel<ManageChatEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(ManageChatState())
    val state = _state.asStateFlow()

    fun onAction(action: ManageChatAction) {
        when (action) {
            ManageChatAction.OnAddClick -> {}
            ManageChatAction.OnCreateChatClick -> {}
            ManageChatAction.OnDismissDialog -> {}
        }
    }
}