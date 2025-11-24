@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.alejo.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.chat.domain.chat.ChatRepository
import dev.alejo.chat.presentation.mappers.toUi
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _events = Channel<ChatDetailEvent>()
    val events = _events.receiveAsFlow()

    private val _chatId = MutableStateFlow<String?>(null)

    private var hasLoadedInitialData = false

    private val chatInfoFlow = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getChatInfoById(chatId)
            } else emptyFlow()
        }

    private val _state = MutableStateFlow(ChatDetailState())

    private val stateWithMessages = combine(
        _state,
        chatInfoFlow,
        sessionStorage.observeAuthInf()
    ) { currentState, chatInfo, authInfo ->
        if (authInfo == null) {
            return@combine ChatDetailState()
        }
        currentState.copy(
            chatUi = chatInfo.chat.toUi(authInfo.user.id)
        )
    }

    val state = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                stateWithMessages
            } else _state
        }
        .onStart {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatDetailState()
        )

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> switchChat(action.chatId)
            ChatDetailAction.OnBackCLick -> {}
            ChatDetailAction.OnChatMembersCLick -> {}
            ChatDetailAction.OnChatOptionsCLick -> onChatOptionsClick()
            is ChatDetailAction.OnDeleteMessageClick -> {}
            ChatDetailAction.OnDismissChatOptions -> onDismissChatOptions()
            ChatDetailAction.OnDismissMessageMenu -> {}
            ChatDetailAction.OnLeaveChatCLick -> onLeaveChatClick()
            is ChatDetailAction.OnMessageLongClick -> {}
            is ChatDetailAction.OnRetryClick -> {}
            ChatDetailAction.OnScrollToTop -> {}
            ChatDetailAction.OnSendMessageClick -> {}
        }
    }

    private fun switchChat(chatId: String?) {
        _chatId.update { chatId }
        viewModelScope.launch {
            chatId?.let {
                chatRepository.fetchChatById(chatId)
            }
        }
    }

    private fun onLeaveChatClick() {
        val chatId = _chatId.value ?: return

        _state.update {
            it.copy(
                isChatOptionsOpen = false
            )
        }

        viewModelScope.launch {
            chatRepository
                .leaveChat(chatId)
                .onSuccess {
                    _state.value.messageTextFieldState.clearText()

                    _chatId.update { null }
                    _state.update {
                        it.copy(
                            chatUi = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }
                    _events.send(ChatDetailEvent.OnChatLeft)
                }
                .onFailure { error ->
                    _events.send(ChatDetailEvent.OnError(error = error.toUiText()))
                }
        }
    }

    private fun onDismissChatOptions() {
        _state.update {
            it.copy(
                isChatOptionsOpen = false
            )
        }
    }

    private fun onChatOptionsClick() {
        _state.update {
            it.copy(
                isChatOptionsOpen = true
            )
        }
    }

}