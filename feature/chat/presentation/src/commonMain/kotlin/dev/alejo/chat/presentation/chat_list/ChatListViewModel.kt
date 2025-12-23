package dev.alejo.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.chat.domain.chat.ChatRepository
import dev.alejo.chat.domain.notification.DeviceTokenService
import dev.alejo.chat.domain.participant.ChatParticipantRepository
import dev.alejo.chat.presentation.mappers.toUi
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val repository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val deviceTokenService: DeviceTokenService,
    private val authService: AuthService,
    private val chatParticipantRepository: ChatParticipantRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _event = Channel<ChatListEvent>()
    val events = _event.receiveAsFlow()

    private val _state = MutableStateFlow(ChatListState())
    val state = combine(
        _state,
        repository.getChats(),
        sessionStorage.observeAuthInf()
    ) { currentState, chats, authInfo ->
        if (authInfo == null) {
            return@combine ChatListState()
        }

        currentState.copy(
            chats = chats.map { it.toUi(authInfo.user.id) },
            localParticipant = authInfo.user.toUi()
        )
    }
        .onStart {
            if (!hasLoadedInitialData) {
                loadLocalChatParticipant()
                loadChats()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListState()
        )

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnSelectChat -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListAction.OnUserAvatarClick -> {
                _state.update {
                    it.copy(
                        isUserMenuOpen = true
                    )
                }
            }

            ChatListAction.OnLogoutClick -> showLogoutConfirmation()

            ChatListAction.OnDismissLogoutDialog -> {
                _state.update {
                    it.copy(
                        isUserMenuOpen = true,
                        showLogoutConfirmation = false
                    )
                }
            }
            ChatListAction.OnConfirmLogout -> logout()

            ChatListAction.OnProfileSettingsCLick,
            ChatListAction.OnDismissUserMenu -> {
                _state.update {
                    it.copy(
                        isUserMenuOpen = false
                    )
                }
            }

            else -> Unit
        }
    }

    private fun loadLocalChatParticipant() {
        viewModelScope.launch {
            chatParticipantRepository
                .fetchLocalParticipant()
        }
    }

    private fun logout() {
        _state.update {
            it.copy(
                showLogoutConfirmation = false,
                isUserMenuOpen = false
            )
        }

        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInf().first()
            val refreshToken = authInfo?.refreshToken ?: return@launch

            deviceTokenService
                .unregisterToken(refreshToken)
                .onSuccess {
                    authService
                        .logout(refreshToken)
                        .onSuccess {
                            sessionStorage.set(null)
                            repository.deleteAllChats()
                            _event.send(ChatListEvent.OnLogoutSuccess)
                        }
                        .onFailure { error ->
                            _event.send(ChatListEvent.OnLogoutError(error.toUiText()))
                        }
                }
                .onFailure { error ->
                    _event.send(ChatListEvent.OnLogoutError(error.toUiText()))
                }
        }
    }

    private fun showLogoutConfirmation() {
        _state.update {
            it.copy(
                showLogoutConfirmation = true
            )
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.fetchChats()
        }
    }

}