package dev.alejo.core.designsystem.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NexoChatBubble(
    messageContent: String,
    sender: String,
    formattedDateTime: String,
    cornerCurvePosition: CornerCurvePosition,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.extended.surfaceHigher,
    messageStatus: @Composable (() -> Unit)? = null,
    cornerCurveSize: Dp = 16.dp,
    onLongClick: (() -> Unit)? = null
) {
    val padding = 12.dp

    Column(
        modifier = modifier
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(
                            color = MaterialTheme.colorScheme.extended.surfaceOutline
                        ),
                        onLongClick = onLongClick,
                        onClick = {}
                    )
                } else Modifier
            )
            .clip(
                ChatBubbleShape(
                    curvePosition = cornerCurvePosition,
                    curveSize = cornerCurveSize
                )
            )
            .background(color)
            .padding(
                start = if (cornerCurvePosition == CornerCurvePosition.LEFT) {
                    padding + cornerCurveSize
                } else padding,
                end = if (cornerCurvePosition == CornerCurvePosition.RIGHT) {
                    padding + cornerCurveSize
                } else padding,
                top = padding,
                bottom = padding
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sender,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = formattedDateTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
            )
        }
        Text(
            text = messageContent,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.extended.textPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        messageStatus?.invoke()
    }
}

@Preview
@Composable
fun NexoChatBubbleLeftPreview() {
    NexoTheme(darkTheme = true) {
        NexoChatBubble(
            messageContent = "Hey, How are you?",
            sender = "Tú",
            formattedDateTime = "Friday 10:00 AM",
            cornerCurvePosition = CornerCurvePosition.LEFT,
        )
    }
}

@Preview
@Composable
fun NexoChatBubbleRightPreview() {
    NexoTheme {
        NexoChatBubble(
            messageContent = "Hey, How are you?",
            sender = "Tú",
            formattedDateTime = "Friday 10:00 AM",
            cornerCurvePosition = CornerCurvePosition.RIGHT,
        )
    }
}