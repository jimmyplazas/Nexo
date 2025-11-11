package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.UiText
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageListItemUi(
    messageUi: MessageUi,
    modifier: Modifier = Modifier,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        when (messageUi) {
            is MessageUi.DateSeparator -> {
                DateSeparatorUi(
                    date = messageUi.date.asString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is MessageUi.LocalUserMessage -> {
                LocalUserMessageUi(
                    message = messageUi,
                    modifier = Modifier.fillMaxWidth(),
                    onMessageLongClick = { onMessageLongClick() },
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = onDeleteClick,
                    onRetryClick = onRetryClick
                )
            }
            is MessageUi.OtherUserMessage -> {
                OtherUserMessageUi(
                    message = messageUi,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DateSeparatorUi(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Text(
            text = date,
            modifier = Modifier.padding(40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textPlaceholder
        )
        HorizontalDivider(Modifier.weight(1f))
    }
}

@Preview
@Composable
fun MessageListItemUiPreview() {
    NexoTheme {
        MessageListItemUi(
            messageUi = MessageUi.DateSeparator(
                id = "1",
                date = UiText.DynamicString("Monday"),
            ),
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview
@Composable
fun MessageListItemUiLocalUserMessagePreview() {
    NexoTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "",
                content = "Hey mate",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                canRetry = false,
                isMenuOpen = true,
                formattedSentTime = UiText.DynamicString("10:00 AM")
            ),
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {}
        )
    }
}

@Preview
@Composable
fun MessageListItemUiOtherUserMessagePreview() {
    NexoTheme {
        MessageListItemUi(
            messageUi = MessageUi.OtherUserMessage(
                id = "1",
                content = "asd",
                formattedSentTime = UiText.DynamicString("10:00 PM"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Alejo",
                    initials = "AL"
                )
            ),
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {}
        )
    }
}