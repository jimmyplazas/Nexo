package dev.alejo.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * The main theme for the Nexo application.
 *
 * This composable function applies the Material 3 theme, setting up the color scheme,
 * typography, and custom extended colors for the entire app. It supports both light and
 * dark themes, automatically detecting the system's setting by default.
 *
 * The custom colors are provided through a [CompositionLocalProvider] for [LocalExtendedColors],
 * allowing them to be accessed throughout the composable hierarchy via `ExtendedColors.current`.
 *
 * @param darkTheme Whether to use the dark theme. Defaults to the system's setting.
 * @param content The composable content to be displayed within this theme.
 */
@Composable
fun NexoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme
    val extendedScheme = if(darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}