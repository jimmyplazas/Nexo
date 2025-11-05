package dev.alejo.nexo

import androidx.compose.runtime.Composable
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.nexo.navigation.NavigationRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    NexoTheme {
        NavigationRoot()
    }
}