package dev.alejo.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.chat_detail.components.ChatDetailHeader
import dev.alejo.chat.presentation.chat_detail.components.MessageBox
import dev.alejo.chat.presentation.chat_detail.components.MessageList
import dev.alejo.chat.presentation.components.ChatHeader
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.clearFocusOnTap
import dev.alejo.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatDetailRoot(
    chatId: String?,
    isDetailPresent: Boolean,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    BackHandler(
        enabled = !isDetailPresent
    ) {
        viewModel.onAction(ChatDetailAction.OnBackCLick)
        onBack()
    }

    ChatDetailScreen(
        state = state,
        isDetailPresent = isDetailPresent,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    isDetailPresent: Boolean,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val messageListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .clearFocusOnTap()
                .padding(innerPadding)
                .then(
                    if (configuration.isWideScreen) {
                        Modifier.padding(horizontal = 8.dp)
                    } else Modifier
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicRoundedCornerBox(
                    isCornersRounded = configuration.isWideScreen,
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                ) {
                    ChatHeader {
                        ChatDetailHeader(
                            chatUi = state.chatUi,
                            isDetailPresent = isDetailPresent,
                            isChatOptionsDropDownOpen = state.isChatOptionsOpen,
                            onChatOptionsClick = { onAction(ChatDetailAction.OnChatOptionsCLick) },
                            onDismissChatOptions = { onAction(ChatDetailAction.OnDismissChatOptions) },
                            onManageChatClick = { onAction(ChatDetailAction.OnChatMembersCLick) },
                            onLeaveChatClick = { onAction(ChatDetailAction.OnLeaveChatCLick) },
                            onBackClick = { onAction(ChatDetailAction.OnBackCLick) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MessageList(
                        messages = state.messages,
                        listState = messageListState,
                        onMessageLongClick = { message ->
                            onAction(ChatDetailAction.OnMessageLongClick(message))
                        },
                        onMessageRetryClick = { message ->
                            onAction(ChatDetailAction.OnRetryClick(message))
                        },
                        onDismissMessageMenu = {
                            onAction(ChatDetailAction.OnDismissMessageMenu)
                        },
                        onDeleteMessageClick = { message ->
                            onAction(ChatDetailAction.OnDeleteMessageClick(message))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    AnimatedVisibility(
                        visible = !configuration.isWideScreen && state.chatUi != null
                    ) {
                        MessageBox(
                            messageTextFieldState = state.messageTextFieldState,
                            isTextInputEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendClick = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }

                if (configuration.isWideScreen) {
                    Spacer(Modifier.height(8.dp))
                }

                AnimatedVisibility(
                    visible = configuration.isWideScreen && state.chatUi != null
                ) {
                    DynamicRoundedCornerBox(
                        isCornersRounded = configuration.isWideScreen
                    ) {
                        MessageBox(
                            messageTextFieldState = state.messageTextFieldState,
                            isTextInputEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendClick = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicRoundedCornerBox(
    isCornersRounded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = if (isCornersRounded) 8.dp else 0.dp,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape,
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape
            )
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatDetailScreenEmptyPreview() {
    NexoTheme {
        ChatDetailScreen(
            state = ChatDetailState(),
            isDetailPresent = false,
            onAction = {}
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview(showBackground = true)
@Composable
private fun ChatDetailScreenMessagesPreview() {
    NexoTheme(darkTheme = true) {
        ChatDetailScreen(
            state = ChatDetailState(
                chatUi = ChatUi(
                    id = "1",
                    localParticipant = ChatParticipantUi(
                        id = "1",
                        username = "alejo",
                        initials = "AL"
                    ),
                    otherParticipants = listOf(
                        ChatParticipantUi(
                            id = "3",
                            username = "Pin",
                            initials = "PI"
                        )
                    )
                ),
                messageTextFieldState = rememberTextFieldState(
                    initialText = "This is a message"
                ),
                canSendMessage = true,
                messages = (1..8).map { number ->
                    val showLocalMessage = Random.nextBoolean()
                    if (showLocalMessage) {
                        MessageUi.LocalUserMessage(
                            id = Uuid.random().toString(),
                            content = "Lorem ipsum",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT,
                            isMenuOpen = false,
                            formattedSentTime = UiText.DynamicString("Friday 10:00 PM"),
                            canRetry = false
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = Uuid.random().toString(),
                            content = "Lorem ipsum",
                            sender = ChatParticipantUi(
                                id = "3",
                                username = "Pin",
                                initials = "PI"
                            ),
                            formattedSentTime = UiText.DynamicString("Friday 10:00 PM")
                        )
                    }
                }.toList()
            ),
            isDetailPresent = true,
            onAction = {}
        )
    }
}