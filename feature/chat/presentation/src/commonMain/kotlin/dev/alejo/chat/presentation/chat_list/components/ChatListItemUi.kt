package dev.alejo.chat.presentation.chat_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.alejo.chat.domain.models.ChatMessage
import dev.alejo.chat.domain.models.ChatMessageDeliveryStatus
import dev.alejo.chat.presentation.components.ChatItemHeaderRow
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.extended.surfaceLower
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ChatItemHeaderRow(
                chat = chat,
                modifier = Modifier.fillMaxWidth()
            )

            chat.lastMessage?.let { lastMessage ->
                val lastMessagePreview = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        if (chat.lastMessageSenderUsername != null) {
                            append(chat.lastMessageSenderUsername + ": ")
                        }
                    }
                    append(lastMessage.content)
                }

                Text(
                    text = lastMessagePreview,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .alpha(if (isSelected) 1f else 0f)
                .background(MaterialTheme.colorScheme.primary)
                .width(4.dp)
                .fillMaxHeight()
        ) {

        }
    }
}

@Preview
@Composable
private fun ChatListItemUiPreview() {
    NexoTheme {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "alejo",
                    initials = "AL"
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Luz",
                        initials = "LU"
                    ),
                    ChatParticipantUi(
                        id = "3",
                        username = "Pin",
                        initials = "PI"
                    )
                )
            ),
            isSelected = true
        )
    }
}

@Preview
@Composable
private fun ChatListItemUiDarkPreview() {
    NexoTheme(darkTheme = true) {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "alejo",
                    initials = "AL"
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Luz",
                        initials = "LU"
                    ),
                    ChatParticipantUi(
                        id = "3",
                        username = "Pin",
                        initials = "PI"
                    )
                ),
                lastMessage = ChatMessage(
                    id = "1",
                    chatId = "2",
                    senderId = "2",
                    content = "Hey mate",
                    createdAt = Clock.System.now(),
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                ),
                lastMessageSenderUsername = "Pin"
            ),
            isSelected = true
        )
    }
}

@Preview
@Composable
private fun ChatListItemUiDarkManyPreview() {
    NexoTheme(darkTheme = true) {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "alejo",
                    initials = "AL"
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Luz",
                        initials = "LU"
                    ),
                    ChatParticipantUi(
                        id = "3",
                        username = "Pin",
                        initials = "PI"
                    ),
                    ChatParticipantUi(
                        id = "3",
                        username = "Josh",
                        initials = "Jo"
                    ),
                    ChatParticipantUi(
                        id = "4",
                        username = "Philipp",
                        initials = "PH"
                    )
                ),
                lastMessage = ChatMessage(
                    id = "1",
                    chatId = "2",
                    senderId = "2",
                    content = "Hey mate",
                    createdAt = Clock.System.now(),
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                ),
                lastMessageSenderUsername = "Pin"
            ),
            isSelected = true
        )
    }
}

@Preview
@Composable
private fun ChatListItemUiDarkOneToOnePreview() {
    NexoTheme(darkTheme = true) {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "alejo",
                    initials = "AL"
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Luz",
                        initials = "LU"
                    )
                ),
                lastMessage = ChatMessage(
                    id = "1",
                    chatId = "2",
                    senderId = "2",
                    content = "All good",
                    createdAt = Clock.System.now(),
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                ),
                lastMessageSenderUsername = "Luz"
            ),
            isSelected = true
        )
    }
}