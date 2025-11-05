package dev.alejo.nexo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.nexo.navigation.DeepLinkListener
import dev.alejo.nexo.navigation.NavigationRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    DeepLinkListener(navController)
    NexoTheme {
        NavigationRoot(navController)
    }
}