package dev.alejo.chat.presentation.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.presentation.chat_list.components.ChatListHeader
import dev.alejo.chat.presentation.chat_list.components.ChatListItemUi
import dev.alejo.chat.presentation.chat_list.components.EmptyChatSection
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.core.designsystem.components.brand.NexoHorizontalDivider
import dev.alejo.core.designsystem.components.buttons.NexoFloatingActionButton
import dev.alejo.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.cancel
import nexo.feature.chat.presentation.generated.resources.create_chat
import nexo.feature.chat.presentation.generated.resources.do_you_want_to_logout
import nexo.feature.chat.presentation.generated.resources.do_you_want_to_logout_desc
import nexo.feature.chat.presentation.generated.resources.logout
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListRoot(
    viewModel: ChatListViewModel = koinViewModel(),
    onChatClick: (ChatUi) -> Unit,
    onConfirmLogoutCLick: () -> Unit,
    onCreateChatCLick: () -> Unit,
    onProfileSettingsCLick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    ChatListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatListAction.OnChatClick -> onChatClick(action.chat)
                ChatListAction.OnConfirmLogout -> onConfirmLogoutCLick()
                ChatListAction.OnCreateChatCLick -> onCreateChatCLick()
                ChatListAction.OnProfileSettingsCLick -> onProfileSettingsCLick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.extended.surfaceLower,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            NexoFloatingActionButton(
                onClick = { onAction(ChatListAction.OnCreateChatCLick) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_chat)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChatListHeader(
                localParticipant = state.localParticipant,
                isUserMenuOpen = state.isUserMenuOpen,
                onUserAvatarClick = {
                    onAction(ChatListAction.OnUserAvatarClick)
                },
                onLogoutClick = {
                    onAction(ChatListAction.OnLogoutClick)
                },
                onDismissMenu = {
                    onAction(ChatListAction.OnDismissUserMenu)
                },
                onProfileSettingsClick = {
                    onAction(ChatListAction.OnProfileSettingsCLick)
                }
            )

            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                state.chats.isEmpty() -> {
                    if (state.chats.isEmpty() && !state.isLoading) {
                        EmptyChatSection(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = state.chats,
                            key = { it.id }
                        ) { chatUi ->
                            ChatListItemUi(
                                chat = chatUi,
                                isSelected = chatUi.id == state.selectedChatId,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable {
                                        onAction(ChatListAction.OnChatClick(chatUi))
                                    }
                            )
                            NexoHorizontalDivider()
                        }
                    }
                }
            }

        }
    }

    if (state.showLogoutConfirmation) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.do_you_want_to_logout),
            description = stringResource(Res.string.do_you_want_to_logout_desc),
            confirmButtonText = stringResource(Res.string.logout),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirm = {
                onAction(ChatListAction.OnConfirmLogout)
            },
            onCancel = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            onDismiss = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        ChatListScreen(
            state = ChatListState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}