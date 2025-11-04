package dev.alejo.nexo

import androidx.compose.runtime.Composable
import dev.alejo.auth.presentation.register.RegisterRoot
import dev.alejo.core.designsystem.theme.NexoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    NexoTheme {
        RegisterRoot() {}
    }
}