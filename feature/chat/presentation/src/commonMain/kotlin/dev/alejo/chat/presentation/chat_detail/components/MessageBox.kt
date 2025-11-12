package dev.alejo.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.alejo.chat.domain.models.ConnectionState
import dev.alejo.chat.presentation.util.toUiText
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.textfields.NexoMultiLineTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.cloud_off_icon
import nexo.feature.chat.presentation.generated.resources.send_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageBox(
    messageTextFieldState: TextFieldState,
    isTextInputEnabled: Boolean,
    connectionState: ConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.CONNECTED

    NexoMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier
            .padding(4.dp),
        placeholder = stringResource(Res.string.send_message),
        enabled = isTextInputEnabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        onKeyboardActions = onSendClick,
        bottomContent = {
            Spacer(Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.cloud_off_icon),
                        contentDescription = connectionState.toUiText().asString(),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled
                    )
                    Text(
                        text = connectionState.toUiText().asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled
                    )
                }
            }
            NexoButton(
                text = stringResource(Res.string.send_message),
                onClick = onSendClick,
                enabled = isConnected && isTextInputEnabled,
            )
        }
    )
}

@Preview
@Composable
fun MessageBoxPreview() {
    NexoTheme {
        MessageBox(
            messageTextFieldState = TextFieldState(),
            isTextInputEnabled = true,
            connectionState = ConnectionState.CONNECTED,
            onSendClick = {}
        )
    }
}