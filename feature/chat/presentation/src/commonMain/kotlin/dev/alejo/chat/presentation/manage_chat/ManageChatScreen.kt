package dev.alejo.chat.presentation.manage_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.presentation.components.manage_chat.ManageChatAction
import dev.alejo.chat.presentation.components.manage_chat.ManageChatScreen
import dev.alejo.chat.presentation.components.manage_chat.ManageChatState
import dev.alejo.core.designsystem.components.dialogs.NexoAdaptiveDialogSheetLayout
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.ObserveAsEvents
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.chat_members
import nexo.feature.chat.presentation.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageChatRoot(
    chatId: String?,
    onDismiss: () -> Unit,
    onMembersAdded: () -> Unit,
    viewModel: ManageChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ManageChatEvent.OnMembersAdded -> onMembersAdded()
        }
    }

    LaunchedEffect(chatId) {
        viewModel.onAction(ManageChatAction.ChatParticipants.OnSelectChat(chatId))
    }

    NexoAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ManageChatScreen(
            headerText = stringResource(Res.string.chat_members),
            primaryButtonText = stringResource(Res.string.save),
            state = state,
            onAction = { action ->
                when(action) {
                    ManageChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Preview
@Composable
fun ManageChatRootPreview() {
    NexoTheme {
        ManageChatScreen(
            headerText = "Chat members",
            primaryButtonText = "Save",
            state = ManageChatState(),
            onAction = {}
        )
    }
}