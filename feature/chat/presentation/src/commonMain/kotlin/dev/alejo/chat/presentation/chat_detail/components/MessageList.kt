package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.components.EmptySection
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.UiText
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.no_messages
import nexo.feature.chat.presentation.generated.resources.no_messages_description
import nexo.feature.chat.presentation.generated.resources.retry
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageList(
    messages: List<MessageUi>,
    paginationError: String?,
    isPaginationLoading: Boolean,
    messageWithOpenMenu: MessageUi.LocalUserMessage?,
    listState: LazyListState,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onRetryPaginationClick: () -> Unit,
    onMessageRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteMessageClick: (MessageUi.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {

    if (messages.isEmpty()) {
        Box(
            modifier = modifier
                .padding(vertical = 32.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptySection(
                title = stringResource(Res.string.no_messages),
                description = stringResource(Res.string.no_messages_description),
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                MessageListItemUi(
                    messageUi = message,
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = onMessageLongClick,
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = onDeleteMessageClick,
                    onRetryClick = onMessageRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }

            when {
                isPaginationLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                paginationError != null -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NexoButton(
                                text = stringResource(Res.string.retry),
                                onClick = onRetryPaginationClick,
                                style = NexoButtonStyle.SECONDARY
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = paginationError,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MessageListPreview() {
    NexoTheme {
        MessageList(
            messages = listOf(
                MessageUi.LocalUserMessage(
                    id = "1",
                    content = "Hey mate!",
                    deliveryStatus = ChatMessageDeliveryStatus.SENT,
                    formattedSentTime = UiText.DynamicString("10:00 AM")
                )
            ),
            messageWithOpenMenu = null,
            modifier = Modifier.fillMaxSize(),
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            listState = LazyListState(),
            isPaginationLoading = false,
            paginationError = null,
            onRetryPaginationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageListEmptyPreview() {
    NexoTheme {
        MessageList(
            messages = emptyList(),
            messageWithOpenMenu = null,
            listState = LazyListState(),
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            modifier = Modifier.fillMaxSize(),
            isPaginationLoading = false,
            paginationError = null,
            onRetryPaginationClick = {}
        )
    }
}