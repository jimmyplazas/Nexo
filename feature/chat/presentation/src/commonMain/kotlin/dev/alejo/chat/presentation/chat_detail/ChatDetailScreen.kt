package dev.alejo.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.chat_detail.components.ChatDetailHeader
import dev.alejo.chat.presentation.chat_detail.components.DateChip
import dev.alejo.chat.presentation.chat_detail.components.MessageBannerListener
import dev.alejo.chat.presentation.chat_detail.components.MessageBox
import dev.alejo.chat.presentation.chat_detail.components.MessageList
import dev.alejo.chat.presentation.chat_detail.components.PaginationScrollListener
import dev.alejo.chat.presentation.components.ChatHeader
import dev.alejo.chat.presentation.components.EmptySection
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.ObserveAsEvents
import dev.alejo.core.presentation.util.UiText
import dev.alejo.core.presentation.util.clearFocusOnTap
import dev.alejo.core.presentation.util.currentDeviceConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.no_chat_selected
import nexo.feature.chat.presentation.generated.resources.select_a_chat
import org.jetbrains.compose.resources.stringResource
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
    onChatMembersClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val messageListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    LaunchedEffect(chatId, state.messages) {
        if (state.messages.isNotEmpty()) {
            messageListState.animateScrollToItem(0)
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatDetailEvent.OnChatLeft -> onBack()
            is ChatDetailEvent.OnError -> {
                snackbarState.showSnackbar(event.error.asStringAsync())
            }
            ChatDetailEvent.OnNewMessage -> {
                scope.launch {
                    messageListState.animateScrollToItem(0)
                }
            }
        }
    }

    BackHandler(
        enabled = !isDetailPresent
    ) {
        // Add artificial delay to prevent detail back animation from showing
        // an unselected chat the moment we go back
        scope.launch {
            delay(300)
            viewModel.onAction(ChatDetailAction.OnBackCLick)
        }
        onBack()
    }

    ChatDetailScreen(
        state = state,
        messageListState = messageListState,
        isDetailPresent = isDetailPresent,
        snackbarState = snackbarState,
        onAction = { action ->
            when (action) {
                ChatDetailAction.OnChatMembersCLick -> onChatMembersClick()
                is ChatDetailAction.OnBackCLick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    messageListState: LazyListState,
    isDetailPresent: Boolean,
    snackbarState: SnackbarHostState,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()

    val realMessageItemCount = remember(state.messages) {
        state
            .messages
            .filter { it is MessageUi.LocalUserMessage || it is MessageUi.OtherUserMessage }
            .size
    }

    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleIndex, totalItemsCount) ->
            firstVisibleIndex >= 0 && totalItemsCount > 0
        }.collect { (firstVisibleIndex, _) ->
            onAction(ChatDetailAction.OnFirstVisibleIndexChanged(firstVisibleIndex))
        }
    }

    MessageBannerListener(
        lazyListState = messageListState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = { index ->
            onAction(ChatDetailAction.OnTopVisibleIndexChanged(index))
        },
        onHide = {
            onAction(ChatDetailAction.OnHideBanner)
        }
    )

    PaginationScrollListener(
        lazyListState = messageListState,
        itemCount = realMessageItemCount,
        isPaginationLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = {
            onAction(ChatDetailAction.OnScrollToTop)
        }
    )

    var headerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
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
                    if (state.chatUi == null) {
                        EmptySection(
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ChatHeader(
                            modifier = Modifier
                                .onSizeChanged {
                                    headerHeight = with(density) {
                                        it.height.toDp()
                                    }
                                }
                        ) {
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
                            messageWithOpenMenu = state.messageWithOpenMenu,
                            paginationError = state.paginationError?.asString(),
                            isPaginationLoading = state.isPaginationLoading,
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
                            onRetryPaginationClick = {
                                onAction(ChatDetailAction.OnRetryPaginationClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        AnimatedVisibility(
                            visible = !configuration.isWideScreen
                        ) {
                            MessageBox(
                                messageTextFieldState = state.messageTextFieldState,
                                isSendButtonEnabled = state.canSendMessage,
                                connectionState = state.connectionState,
                                onSendClick = {
                                    onAction(ChatDetailAction.OnSendMessageClick)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
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
                            isSendButtonEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendClick = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = headerHeight + 16.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString()
                    )
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
            snackbarState = remember { SnackbarHostState() },
            onAction = {},
            messageListState = rememberLazyListState()
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
                            formattedSentTime = UiText.DynamicString("Friday 10:00 PM"),
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
            snackbarState = remember { SnackbarHostState() },
            onAction = {},
            messageListState = rememberLazyListState()
        )
    }
}