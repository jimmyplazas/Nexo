package dev.alejo.core.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import dev.alejo.core.presentation.util.currentDeviceConfiguration

@Composable
fun NexoAdaptiveDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if (configuration.isMobile) {
        NexoBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        NexoDialogContent(
            onDismiss = onDismiss,
            content = content
        )
    }

}