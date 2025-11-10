package dev.alejo.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.alejo.core.designsystem.theme.NexoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NexoStackedAvatars(
    avatars: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercentage: Float = 0.4f,
) {
    val overlapOffset = -(size.dp * overlapPercentage)

    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatar ->
            NexoAvatarPhoto(
                displayText = avatar.initials,
                imageUrl = avatar.imageUrl,
                size = size
            )
        }

        if (remainingCount > 0) {
            NexoAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun NexoStackedAvatarsPreview() {
    NexoTheme {
        NexoStackedAvatars(
            avatars = listOf(
                ChatParticipantUi(
                    id = "1",
                    initials = "AA",
                    username = "Alejo"
                ),
                ChatParticipantUi(
                    id = "2",
                    initials = "BB",
                    username = "Bernadr"
                ),
                ChatParticipantUi(
                    id = "3",
                    initials = "CC",
                    username = "Charlie"
                ),
                ChatParticipantUi(
                    id = "4",
                    initials = "DD",
                    username = "Daniel"
                )
            )
        )
    }
}