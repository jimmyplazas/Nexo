package dev.alejo.chat.presentation.create_chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.presentation.components.ChatParticipantSearchTextSection
import dev.alejo.chat.presentation.components.ChatParticipantsSelectionSection
import dev.alejo.chat.presentation.components.ManageChatBottomSection
import dev.alejo.chat.presentation.components.ManageChatHeaderRow
import dev.alejo.core.designsystem.components.brand.NexoHorizontalDivider
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.components.dialogs.NexoAdaptiveDialogSheetLayout
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.DeviceConfiguration
import dev.alejo.core.presentation.util.clearFocusOnTap
import dev.alejo.core.presentation.util.currentDeviceConfiguration
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.cancel
import nexo.feature.chat.presentation.generated.resources.create_chat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    viewModel: CreateChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    NexoAdaptiveDialogSheetLayout(
        onDismiss = {
            viewModel.onAction(CreateChatAction.OnDismissDialog)
        }
    ) {
        CreateChatScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun CreateChatScreen(
    state: CreateChatState,
    onAction: (CreateChatAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()

    val shouldHideHeader = configuration == DeviceConfiguration.MOBILE_LANDSCAPE
            || (isKeyboardVisible && configuration != DeviceConfiguration.DESKTOP) || isTextFieldFocused

    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxWidth()
            .wrapContentWidth()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ManageChatHeaderRow(
                    title = stringResource(Res.string.create_chat),
                    onCloseClick = { onAction(CreateChatAction.OnDismissDialog) },
                    modifier = Modifier.fillMaxWidth()
                )
                NexoHorizontalDivider()
            }
        }
        ChatParticipantSearchTextSection(
            queryState = state.queryTextState,
            onAddClick = {
                onAction(CreateChatAction.OnAddClick)
            },
            isSearchEnabled = state.canAddParticipants,
            isLoading = state.isAddingParticipant,
            modifier = Modifier
                .fillMaxWidth(),
            error = state.searchError,
            onFocusChanges = {
                isTextFieldFocused = it
            }
        )
        NexoHorizontalDivider()
        ChatParticipantsSelectionSection(
            selectedParticipants = state.selectedChatParticipants,
            modifier = Modifier
                .fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        NexoHorizontalDivider()
        ManageChatBottomSection(
            primaryButton = {
                NexoButton(
                    text = stringResource(Res.string.create_chat),
                    onClick = { onAction(CreateChatAction.OnCreateChatClick) },
                    enabled = state.selectedChatParticipants.isNotEmpty(),
                    isLoading = state.isCreatingChat
                )
            },
            secondaryButton = {
                NexoButton(
                    text = stringResource(Res.string.cancel),
                    style = NexoButtonStyle.SECONDARY,
                    onClick = { onAction(CreateChatAction.OnDismissDialog) }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        CreateChatScreen(
            state = CreateChatState(),
            onAction = {}
        )
    }
}