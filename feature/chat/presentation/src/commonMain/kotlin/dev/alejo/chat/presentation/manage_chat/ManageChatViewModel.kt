@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package dev.alejo.chat.presentation.manage_chat

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alejo.chat.domain.participant.ChatParticipantService
import dev.alejo.chat.domain.chat.ChatRepository
import dev.alejo.chat.presentation.components.manage_chat.ManageChatAction
import dev.alejo.chat.presentation.components.manage_chat.ManageChatState
import dev.alejo.chat.presentation.mappers.toUi
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.error_participant_not_found
import kotlin.time.Duration.Companion.seconds

class ManageChatViewModel(
    private val chatRepository: ChatRepository,
    private val chatParticipantService: ChatParticipantService
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _chatId = MutableStateFlow<String?>(null)

    private val _events = Channel<ManageChatEvent>()
    val events = _events.receiveAsFlow()

    private val searchFLow = snapshotFlow { _state.value.queryTextState.text.toString() }
        .debounce(1.seconds)
        .onEach { query ->
            performSearch(query)
        }

    private val _state = MutableStateFlow(ManageChatState())
    val state = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getActiveParticipantsByChatId(chatId)
            } else emptyFlow()
        }
        .combine(_state) { participants, currentState ->
            currentState.copy(
                existingChatParticipants = participants.map { it.toUi() }
            )
        }
        .onStart {
            if (!hasLoadedInitialData) {
                searchFLow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ManageChatState()
        )

    fun onAction(action: ManageChatAction) {
        when (action) {
            ManageChatAction.OnAddClick -> addParticipantsToChat()
            ManageChatAction.OnPrimaryActionClick -> addParticipant()
            is ManageChatAction.ChatParticipants.OnSelectChat -> {
                _chatId.update { action.chatId }
            }
            else -> Unit
        }
    }

    private fun addParticipant() {
        if (!state.value.canAddParticipant && state.value.isSearching) {
            return
        }

        state.value.currentSearchResult?.let { participantFromSearch ->
            val isAlreadySelected = state.value.selectedChatParticipants.any {
                it.id == participantFromSearch.id
            }
            val isAlreadyInChat = state.value.existingChatParticipants.any {
                it.id == participantFromSearch.id
            }
            val updatedParticipants = if (isAlreadyInChat || isAlreadySelected) {
                state.value.selectedChatParticipants
            } else {
                state.value.selectedChatParticipants + participantFromSearch
            }

            state.value.queryTextState.clearText()

            _state.update {
                it.copy(
                    selectedChatParticipants = updatedParticipants,
                    currentSearchResult = null,
                    canAddParticipant = false
                )
            }
        }
    }

    private fun addParticipantsToChat() {
        if (state.value.selectedChatParticipants.isEmpty()) {
            return
        }

        val chatId = _chatId.value ?: return

        val selectedParticipants = state.value.selectedChatParticipants
        val selectedUserIds = selectedParticipants.map { it.id }

        viewModelScope.launch {
            chatRepository
                .addParticipantsToChat(
                    chatId = chatId,
                    userIds = selectedUserIds
                )
                .onSuccess {
                    _events.send(ManageChatEvent.OnMembersAdded)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.toUiText()
                        )
                    }
                }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update {
                it.copy(
                    currentSearchResult = null,
                    canAddParticipant = false,
                    searchError = null
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSearching = true,
                    canAddParticipant = false
                )
            }

            chatParticipantService
                .searchParticipant(query)
                .onSuccess { participant ->
                    _state.update {
                        it.copy(
                            currentSearchResult = participant.toUi(),
                            isSearching = false,
                            canAddParticipant = true,
                            searchError = null
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.NOT_FOUND -> {
                            UiText.Resource(Res.string.error_participant_not_found)
                        }
                        else -> error.toUiText()
                    }

                    _state.update {
                        it.copy(
                            isSearching = false,
                            canAddParticipant = false,
                            currentSearchResult = null,
                            searchError = errorMessage
                        )
                    }
                }
        }
    }
}