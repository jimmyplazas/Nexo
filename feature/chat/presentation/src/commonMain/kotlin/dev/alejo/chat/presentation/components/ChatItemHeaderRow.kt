package dev.alejo.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.components.avatar.NexoStackedAvatars
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.designsystem.theme.titleXSmall
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.group_chat
import nexo.feature.chat.presentation.generated.resources.only_you
import nexo.feature.chat.presentation.generated.resources.you
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatItemHeaderRow(
    chat: ChatUi,
    modifier: Modifier = Modifier
) {
    val isGroupChat = chat.otherParticipants.size > 1

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (chat.otherParticipants.isNotEmpty()) {
            NexoStackedAvatars(
                avatars = chat.otherParticipants
            )
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (!isGroupChat) {
                    chat.otherParticipants.firstOrNull()?.username
                        ?: stringResource(Res.string.only_you)
                } else {
                    stringResource(Res.string.group_chat)
                },
                style = MaterialTheme.typography.titleXSmall,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            if (isGroupChat) {
                val you = stringResource(Res.string.you)
                val formattedUsernames = remember(chat.otherParticipants) {
                    "$you, " + chat.otherParticipants.joinToString { it.username }
                }
                Text(
                    text = formattedUsernames,
                    color = MaterialTheme.colorScheme.extended.textPlaceholder,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatItemHeaderRowPreview() {
    NexoTheme {
        ChatItemHeaderRow(
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
            )
        )
    }
}