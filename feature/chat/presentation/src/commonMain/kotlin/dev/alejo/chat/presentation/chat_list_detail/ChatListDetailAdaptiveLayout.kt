@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)

package dev.alejo.chat.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.presentation.chat_detail.ChatDetailRoot
import dev.alejo.chat.presentation.chat_list.ChatListRoot
import dev.alejo.chat.presentation.create_chat.CreateChatRoot
import dev.alejo.chat.presentation.manage_chat.ManageChatRoot
import dev.alejo.chat.presentation.profile.ProfileRoot
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.DialogSheetScopeViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveLayout(
    initialChatId: String?,
    onLogout: () -> Unit,
    chatListDetailViewModel: ChatListDetailViewModel = koinViewModel()
) {
    val sharedState by chatListDetailViewModel.state.collectAsStateWithLifecycle()

    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(initialChatId) {
        if (initialChatId != null) {
            chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(initialChatId))
            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
    }

    BackHandler(enabled = scaffoldNavigator.canNavigateBack()) {
        scope.launch {
            scaffoldNavigator.navigateBack()
            chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(null))
        }
    }

    val detailPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail]

    LaunchedEffect(detailPane, sharedState.selectedChatId != null) {
        if (detailPane == PaneAdaptedValue.Hidden && sharedState.selectedChatId != null) {
            chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(null))
        }
    }

    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                ChatListRoot(
                    selectedSChatId = sharedState.selectedChatId,
                    onChatClick = { chatId ->
                        chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(chatId))
                        scope.launch {
                            scaffoldNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail
                            )
                        }
                    },
                    onConfirmLogoutCLick = onLogout,
                    onCreateChatCLick = {
                        chatListDetailViewModel.onAction(ChatListDetailAction.OnCreateChatClick)
                    },
                    onProfileSettingsCLick = {
                        chatListDetailViewModel.onAction(ChatListDetailAction.OnProfileSettingsClick)
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val listPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
                ChatDetailRoot(
                    chatId = sharedState.selectedChatId,
                    isDetailPresent = detailPane == PaneAdaptedValue.Expanded && listPane == PaneAdaptedValue.Expanded,
                    onChatMembersClick = {
                        chatListDetailViewModel.onAction(ChatListDetailAction.OnManageChatClick)
                    },
                    onBack = {
                        scope.launch {
                            if (scaffoldNavigator.canNavigateBack()) {
                                scaffoldNavigator.navigateBack()
                            }
                        }
                    }
                )
            }
        }
    )

    DialogSheetScopeViewModel(
        visible = sharedState.dialogState is DialogState.CreateChat
    ) {
        CreateChatRoot(
            onDismiss = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
            },
            onCreateChat = { chat ->
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
                chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(chat.id))
                scope.launch {
                    scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            }
        )
    }

    DialogSheetScopeViewModel(
        visible = sharedState.dialogState is DialogState.ManageChat
    ) {
        ManageChatRoot(
            chatId = sharedState.selectedChatId,
            onDismiss = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
            },
            onMembersAdded = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
            }
        )
    }

    DialogSheetScopeViewModel(
        visible = sharedState.dialogState is DialogState.Profile
    ) {
        ProfileRoot(
            onDismiss = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
            }
        )
    }
}