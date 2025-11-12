package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alejo.chat.presentation.components.ChatHeader
import dev.alejo.chat.presentation.components.ChatItemHeaderRow
import dev.alejo.chat.presentation.model.ChatUi
import dev.alejo.core.designsystem.components.avatar.ChatParticipantUi
import dev.alejo.core.designsystem.components.buttons.NexoIconButton
import dev.alejo.core.designsystem.components.dropdowns.DropDownItem
import dev.alejo.core.designsystem.components.dropdowns.NexoDropDownMenu
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.core.designsystem.generated.resources.arrow_left_icon
import nexo.core.designsystem.generated.resources.dots_icon
import nexo.core.designsystem.generated.resources.log_out_icon
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.chat_members
import nexo.feature.chat.presentation.generated.resources.go_back
import nexo.feature.chat.presentation.generated.resources.leave_chat
import nexo.feature.chat.presentation.generated.resources.open_chat_options_menu
import nexo.feature.chat.presentation.generated.resources.users_icon
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import nexo.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi,
    isDetailPresent: Boolean,
    isChatOptionsDropDownOpen: Boolean,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: () -> Unit,
    onManageChatClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isDetailPresent) {
            NexoIconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }

        ChatItemHeaderRow(
            chat = chatUi,
            modifier = Modifier.weight(1f)
                .clickable {
                    onManageChatClick()
                }
        )

        Box {
            NexoIconButton(
                onClick = onChatOptionsClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            NexoDropDownMenu(
                isOpen = isChatOptionsDropDownOpen,
                onDismiss = onDismissChatOptions,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageChatClick,
                    ),
                    DropDownItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick,
                    )
                )
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatDetailHeaderPreview() {
    NexoTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatHeader {
                ChatDetailHeader(
                    chatUi = ChatUi(
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
                    isDetailPresent = false,
                    isChatOptionsDropDownOpen = true,
                    onChatOptionsClick = {},
                    onDismissChatOptions = {},
                    onManageChatClick = {},
                    onLeaveChatClick = {},
                    onBackClick = {}
                )
            }
        }
    }
}