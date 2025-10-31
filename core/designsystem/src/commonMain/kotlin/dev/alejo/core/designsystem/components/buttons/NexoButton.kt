package dev.alejo.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
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
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = {}
) {
    val colors = when(style) {
        NexoButtonStyle.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        NexoButtonStyle.DESTRUCTIVE_PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        NexoButtonStyle.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        NexoButtonStyle.DESTRUCTIVE_SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        NexoButtonStyle.TEXT -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
    }

    val defaultBorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )

    val border = when {
        style == NexoButtonStyle.PRIMARY && !isEnabled -> defaultBorderStroke
        style == NexoButtonStyle.SECONDARY && !isEnabled -> defaultBorderStroke
        style == NexoButtonStyle.DESTRUCTIVE_PRIMARY && !isEnabled -> defaultBorderStroke
        style == NexoButtonStyle.DESTRUCTIVE_SECONDARY -> {
            val borderColor = if(isEnabled) {
                MaterialTheme.colorScheme.extended.destructiveSecondaryOutline
            } else {
                MaterialTheme.colorScheme.extended.disabledOutline
            }
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        border = border
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.5.dp,
                color = Color.Black
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(
                    if (isLoading) 0f else 1f
                )
            ) {
                leadingIcon?.let {
                    it.invoke()
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
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