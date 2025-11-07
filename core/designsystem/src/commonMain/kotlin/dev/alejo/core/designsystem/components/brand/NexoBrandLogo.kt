package dev.alejo.core.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nexo.core.designsystem.generated.resources.Res
import nexo.core.designsystem.generated.resources.nexo_logo
import org.jetbrains.compose.resources.vectorResource

@Composable
fun NexoBrandLogo(modifier: Modifier = Modifier) {
    Icon(
        imageVector = vectorResource(Res.drawable.nexo_logo),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}