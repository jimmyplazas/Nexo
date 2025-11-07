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
    avatars: List<AvatarUi>,
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
                AvatarUi(
                    id = "1",
                    initials = "AA",
                    username = "Alejo"
                ),
                AvatarUi(
                    id = "2",
                    initials = "BB",
                    username = "Bernadr"
                ),
                AvatarUi(
                    id = "3",
                    initials = "CC",
                    username = "Charlie"
                ),
                AvatarUi(
                    id = "4",
                    initials = "DD",
                    username = "Daniel"
                )
            )
        )
    }
}