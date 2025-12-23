package dev.alejo.core.designsystem.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.core.designsystem.generated.resources.Res
import nexo.core.designsystem.generated.resources.dismiss_dialog
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DestructiveConfirmationDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .widthIn(max = 480.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary

                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    NexoButton(
                        text = cancelButtonText,
                        onClick = onCancel,
                        style = NexoButtonStyle.DESTRUCTIVE_SECONDARY
                    )
                    NexoButton(
                        text = confirmButtonText,
                        onClick = onConfirm,
                        style = NexoButtonStyle.DESTRUCTIVE_PRIMARY
                    )
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.dismiss_dialog),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}

@Preview
@Composable
fun DestructiveConfirmationDialogPreview() {
    NexoTheme {
        DestructiveConfirmationDialog(
            title = "Delete profile picture?",
            description = "this will permanently delete your profile picture. This cannot be undone.",
            confirmButtonText = "Delete",
            cancelButtonText = "Cancel",
            onConfirm = {},
            onCancel = {},
            onDismiss = {}
        )
    }
}