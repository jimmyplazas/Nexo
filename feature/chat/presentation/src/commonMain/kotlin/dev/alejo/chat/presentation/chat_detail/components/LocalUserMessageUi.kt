package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.core.designsystem.components.chat.CornerCurvePosition
import dev.alejo.core.designsystem.components.chat.NexoChatBubble
import dev.alejo.core.designsystem.components.dropdowns.DropDownItem
import dev.alejo.core.designsystem.components.dropdowns.NexoDropDownMenu
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.UiText
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.delete_for_everyone
import nexo.feature.chat.presentation.generated.resources.reload_icon
import nexo.feature.chat.presentation.generated.resources.retry
import nexo.feature.chat.presentation.generated.resources.you
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LocalUserMessageUi(
    message: MessageUi.LocalUserMessage,
    messageWithOpenMenu: MessageUi.LocalUserMessage?,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.End)
    ) {
        Box {
            NexoChatBubble(
                messageContent = message.content,
                sender = stringResource(Res.string.you),
                formattedDateTime = message.formattedSentTime.asString(),
                cornerCurvePosition = CornerCurvePosition.RIGHT,
                messageStatus = {
                    MessageStatusUi(
                        status = message.deliveryStatus
                    )
                },
                onLongClick = onMessageLongClick
            )

            val dropDownMenuItems = listOf(
                DropDownItem(
                    title = stringResource(Res.string.delete_for_everyone),
                    onClick = onDeleteClick,
                    icon = Icons.Default.Delete,
                    contentColor = MaterialTheme.colorScheme.extended.destructiveHover
                )
            )

            NexoDropDownMenu(
                isOpen = messageWithOpenMenu?.id == message.id,
                items = dropDownMenuItems,
                onDismiss = onDismissMessageMenu
            )
        }

        if (message.deliveryStatus == ChatMessageDeliveryStatus.FAILED) {
            IconButton(
                onClick = onRetryClick
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.reload_icon),
                    contentDescription = stringResource(Res.string.retry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
fun LocalUserMessageUiFailedPreview() {
    NexoTheme {
        LocalUserMessageUi(
            message = MessageUi.LocalUserMessage(
                id = "",
                content = "Hey mate",
                deliveryStatus = ChatMessageDeliveryStatus.FAILED,
                formattedSentTime = UiText.DynamicString("10:00 AM")
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = { },
            onDismissMessageMenu = { },
            onDeleteClick = { },
            onRetryClick = { }
        )
    }
}

@Preview
@Composable
fun LocalUserMessageUiFailedRetryPreview() {
    NexoTheme {
        LocalUserMessageUi(
            message = MessageUi.LocalUserMessage(
                id = "",
                content = "Hey mate",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("10:00 AM")
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = { },
            onDismissMessageMenu = { },
            onDeleteClick = { },
            onRetryClick = { }
        )
    }
}

@Preview
@Composable
fun LocalUserMessageUiSentPreview() {
    NexoTheme {
        LocalUserMessageUi(
            message = MessageUi.LocalUserMessage(
                id = "",
                content = "Hey mate",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("10:00 AM")
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = { },
            onDismissMessageMenu = { },
            onDeleteClick = { },
            onRetryClick = { }
        )
    }
}

@Preview
@Composable
fun LocalUserMessageUiSendingPreview() {
    NexoTheme {
        LocalUserMessageUi(
            message = MessageUi.LocalUserMessage(
                id = "",
                content = "Hey mate",
                deliveryStatus = ChatMessageDeliveryStatus.SENDING,
                formattedSentTime = UiText.DynamicString("10:00 AM")
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = { },
            onDismissMessageMenu = { },
            onDeleteClick = { },
            onRetryClick = { }
        )
    }
}