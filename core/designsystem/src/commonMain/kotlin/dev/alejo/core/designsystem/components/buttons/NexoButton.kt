package dev.alejo.core.designsystem.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alejo.core.designsystem.theme.NexoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class NexoButtonStyle {
    PRIMARY,
    DESTRUCTIVE_PRIMARY,
    SECONDARY,
    DESTRUCTIVE_SECONDARY,
    TEXT
}

@Composable
fun NexoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NexoButtonStyle = NexoButtonStyle.PRIMARY,
    isEnabled: Boolean = false,
    isLoading: Boolean = false,
    leadingIcon: @Composable () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp)
    ) {

    }
}

@Preview
@Composable
private fun NexoPrimaryButtonPreview() {
    NexoTheme {
        NexoButton(
            text = "Hello world",
            onClick = {},
            style = NexoButtonStyle.PRIMARY
        )
    }
}

@Preview
@Composable
private fun NexoSecondaryButtonPreview() {
    NexoTheme {
        NexoButton(
            text = "Hello world",
            onClick = {},
            style = NexoButtonStyle.SECONDARY
        )
    }
}

@Preview
@Composable
private fun NexoDestructivePrimaryButtonPreview() {
    NexoTheme {
        NexoButton(
            text = "Hello world",
            onClick = {},
            style = NexoButtonStyle.DESTRUCTIVE_PRIMARY
        )
    }
}

@Preview
@Composable
private fun NexoDestructiveSecondaryButtonPreview() {
    NexoTheme {
        NexoButton(
            text = "Hello world",
            onClick = {},
            style = NexoButtonStyle.DESTRUCTIVE_SECONDARY
        )
    }
}

@Preview
@Composable
private fun NexoTextButtonPreview() {
    NexoTheme {
        NexoButton(
            text = "Hello world",
            onClick = {},
            style = NexoButtonStyle.TEXT
        )
    }
}