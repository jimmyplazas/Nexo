package dev.alejo.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * A set of extended colors to complement the Material Design [ColorScheme].
 *
 * This property provides access to custom colors that are not part of the standard
 * Material Theme, such as specific hover states, text variants, surface tones,
 * and accent palettes.
 *
 * Usage:
 * ```
 * MaterialTheme.colorScheme.extended.primaryHover
 * ```
 */
val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,

    // Accent colors
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,

    // Cake colors for chat bubbles
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
)

val LightExtendedColors = ExtendedColors(
    primaryHover = NexoBrand600,
    destructiveHover = NexoRed600,
    destructiveSecondaryOutline = NexoRed200,
    disabledOutline = NexoBase200,
    disabledFill = NexoBase150,
    successOutline = NexoBrand100,
    success = NexoBrand600,
    onSuccess = NexoBase0,
    secondaryFill = NexoBase100,

    textPrimary = NexoBase1000,
    textTertiary = NexoBase800,
    textSecondary = NexoBase900,
    textPlaceholder = NexoBase700,
    textDisabled = NexoBase400,

    surfaceLower = NexoBase100,
    surfaceHigher = NexoBase100,
    surfaceOutline = NexoBase1000Alpha14,
    overlay = NexoBase1000Alpha80,

    accentBlue = NexoBlue,
    accentPurple = NexoPurple,
    accentViolet = NexoViolet,
    accentPink = NexoPink,
    accentOrange = NexoOrange,
    accentYellow = NexoYellow,
    accentGreen = NexoGreen,
    accentTeal = NexoTeal,
    accentLightBlue = NexoLightBlue,
    accentGrey = NexoGrey,

    cakeViolet = NexoCakeLightViolet,
    cakeGreen = NexoCakeLightGreen,
    cakeBlue = NexoCakeLightBlue,
    cakePink = NexoCakeLightPink,
    cakeOrange = NexoCakeLightOrange,
    cakeYellow = NexoCakeLightYellow,
    cakeTeal = NexoCakeLightTeal,
    cakePurple = NexoCakeLightPurple,
    cakeRed = NexoCakeLightRed,
    cakeMint = NexoCakeLightMint,
)

val DarkExtendedColors = ExtendedColors(
    primaryHover = NexoBrand600,
    destructiveHover = NexoRed600,
    destructiveSecondaryOutline = NexoRed200,
    disabledOutline = NexoBase900,
    disabledFill = NexoBase1000,
    successOutline = NexoBrand500Alpha40,
    success = NexoBrand500,
    onSuccess = NexoBase1000,
    secondaryFill = NexoBase900,

    textPrimary = NexoBase0,
    textTertiary = NexoBase200,
    textSecondary = NexoBase150,
    textPlaceholder = NexoBase400,
    textDisabled = NexoBase500,

    surfaceLower = NexoBase1000,
    surfaceHigher = NexoBase900,
    surfaceOutline = NexoBase100Alpha10Alt,
    overlay = NexoBase1000Alpha80,

    accentBlue = NexoBlue,
    accentPurple = NexoPurple,
    accentViolet = NexoViolet,
    accentPink = NexoPink,
    accentOrange = NexoOrange,
    accentYellow = NexoYellow,
    accentGreen = NexoGreen,
    accentTeal = NexoTeal,
    accentLightBlue = NexoLightBlue,
    accentGrey = NexoGrey,

    cakeViolet = NexoCakeDarkViolet,
    cakeGreen = NexoCakeDarkGreen,
    cakeBlue = NexoCakeDarkBlue,
    cakePink = NexoCakeDarkPink,
    cakeOrange = NexoCakeDarkOrange,
    cakeYellow = NexoCakeDarkYellow,
    cakeTeal = NexoCakeDarkTeal,
    cakePurple = NexoCakeDarkPurple,
    cakeRed = NexoCakeDarkRed,
    cakeMint = NexoCakeDarkMint,
)

val LightColorScheme = lightColorScheme(
    primary = NexoBrand500,
    onPrimary = NexoBrand1000,
    primaryContainer = NexoBrand100,
    onPrimaryContainer = NexoBrand900,

    secondary = NexoBase700,
    onSecondary = NexoBase0,
    secondaryContainer = NexoBase100,
    onSecondaryContainer = NexoBase900,

    tertiary = NexoBrand900,
    onTertiary = NexoBase0,
    tertiaryContainer = NexoBrand100,
    onTertiaryContainer = NexoBrand1000,

    error = NexoRed500,
    onError = NexoBase0,
    errorContainer = NexoRed200,
    onErrorContainer = NexoRed600,

    background = NexoBrand1000,
    onBackground = NexoBase0,
    surface = NexoBase0,
    onSurface = NexoBase1000,
    surfaceVariant = NexoBase100,
    onSurfaceVariant = NexoBase900,

    outline = NexoBase1000Alpha8,
    outlineVariant = NexoBase200,
)

val DarkColorScheme = darkColorScheme(
    primary = NexoBrand500,
    onPrimary = NexoBrand1000,
    primaryContainer = NexoBrand900,
    onPrimaryContainer = NexoBrand500,

    secondary = NexoBase400,
    onSecondary = NexoBase1000,
    secondaryContainer = NexoBase900,
    onSecondaryContainer = NexoBase150,

    tertiary = NexoBrand500,
    onTertiary = NexoBase1000,
    tertiaryContainer = NexoBrand900,
    onTertiaryContainer = NexoBrand500,

    error = NexoRed500,
    onError = NexoBase0,
    errorContainer = NexoRed600,
    onErrorContainer = NexoRed200,

    background = NexoBase1000,
    onBackground = NexoBase0,
    surface = NexoBase950,
    onSurface = NexoBase0,
    surfaceVariant = NexoBase900,
    onSurfaceVariant = NexoBase150,

    outline = NexoBase100Alpha10,
    outlineVariant = NexoBase800,
)