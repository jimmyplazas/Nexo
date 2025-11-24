package dev.alejo.chat.presentation.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.domain.models.Chat
import dev.alejo.chat.presentation.components.manage_chat.ManageChatAction
import dev.alejo.chat.presentation.components.manage_chat.ManageChatScreen
import dev.alejo.core.designsystem.components.dialogs.NexoAdaptiveDialogSheetLayout
import dev.alejo.core.presentation.util.ObserveAsEvents
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.create_chat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    viewModel: CreateChatViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    onCreateChat: (Chat) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateChatEvent.OnChatCreated -> onCreateChat(event.chat)
        }
    }

    NexoAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ManageChatScreen(
            headerText = stringResource(Res.string.create_chat),
            primaryButtonText = stringResource(Res.string.create_chat),
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