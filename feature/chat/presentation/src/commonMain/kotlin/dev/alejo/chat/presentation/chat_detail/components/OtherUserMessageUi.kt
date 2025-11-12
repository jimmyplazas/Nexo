package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alejo.chat.presentation.model.MessageUi
import dev.alejo.chat.presentation.util.getChatBubbleColorForUser
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.components.avatar.NexoAvatarPhoto
import dev.alejo.core.designsystem.components.chat.CornerCurvePosition
import dev.alejo.core.designsystem.components.chat.NexoChatBubble
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.UiText
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun OtherUserMessageUi(
    message: MessageUi.OtherUserMessage,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        NexoAvatarPhoto(
            displayText = message.sender.initials,
            imageUrl = message.sender.imageUrl,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        NexoChatBubble(
            messageContent = message.content,
            sender = message.sender.username,
            color = color,
            cornerCurvePosition = CornerCurvePosition.LEFT,
            formattedDateTime = message.formattedSentTime.asString()
        )
    }
}

@Preview
@Composable
private fun OtherUserMessageUiPreview() {
    NexoTheme {
        OtherUserMessageUi(
            message = MessageUi.OtherUserMessage(
                id = "1",
                content = "asd",
                formattedSentTime = UiText.DynamicString("10:00 PM"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Alejo",
                    initials = "AL"
                ),
            ),
            color = getChatBubbleColorForUser("1")
        )
    }
}