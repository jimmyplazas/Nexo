package dev.alejo.core.designsystem.components.dropdowns

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.alejo.core.designsystem.components.brand.NexoHorizontalDivider
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NexoDropDownMenu(
    isOpen: Boolean,
    items: List<DropDownItem>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isOpen,
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.extended.surfaceOutline
        ),
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.title,
                            color = item.contentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                onClick =  {
                    onDismiss()
                    item.onClick()
                }
            )

            if (index != items.lastIndex) {
                NexoHorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
fun NexoDropDownMenuPreview() {
    NexoTheme {
        NexoDropDownMenu(
            isOpen = false,
            items = emptyList(),
            onDismiss = { },
        )
    }
}