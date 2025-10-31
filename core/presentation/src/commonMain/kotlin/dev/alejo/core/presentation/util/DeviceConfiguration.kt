package dev.alejo.core.presentation.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * A Composable function that determines the current [DeviceConfiguration] based on the window size.
 *
 * This function utilizes [currentWindowAdaptiveInfo] to get the `WindowSizeClass` and then maps it
 * to a more specific [DeviceConfiguration] enum, such as `MOBILE_PORTRAIT`, `TABLET_LANDSCAPE`, etc.
 * This is useful for creating adaptive layouts that respond to changes in screen size and orientation.
 *
 * @return The current [DeviceConfiguration] of the device window.
 */
@Composable
fun currentDeviceConfiguration(): DeviceConfiguration {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
}

/**
 * Represents the different device configurations based on screen size and orientation.
 * This enum helps in creating adaptive layouts for various form factors.
 *
 * The configurations are derived from [WindowSizeClass] breakpoints.
 *
 * @see MOBILE_PORTRAIT For typical phones in portrait mode.
 * @see MOBILE_LANDSCAPE For typical phones in landscape mode.
 * @see TABLET_PORTRAIT For tablets or large foldables in portrait mode.
 * @see TABLET_LANDSCAPE For tablets or large foldables in landscape mode.
 * @see DESKTOP For large screens like desktops or when the configuration doesn't fit other categories.
 */
enum class DeviceConfiguration {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    DESKTOP;

    companion object {
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            return with(windowSizeClass) {
                when {
                    minWidthDp < WIDTH_DP_MEDIUM_LOWER_BOUND &&
                            minHeightDp >= HEIGHT_DP_MEDIUM_LOWER_BOUND -> MOBILE_PORTRAIT
                    minWidthDp >= WIDTH_DP_EXPANDED_LOWER_BOUND &&
                            minHeightDp < HEIGHT_DP_MEDIUM_LOWER_BOUND -> MOBILE_LANDSCAPE
                    minWidthDp in WIDTH_DP_MEDIUM_LOWER_BOUND..WIDTH_DP_EXPANDED_LOWER_BOUND &&
                            minHeightDp >= HEIGHT_DP_EXPANDED_LOWER_BOUND -> TABLET_PORTRAIT
                    minWidthDp >= WIDTH_DP_EXPANDED_LOWER_BOUND &&
                            minHeightDp in HEIGHT_DP_MEDIUM_LOWER_BOUND..HEIGHT_DP_EXPANDED_LOWER_BOUND -> TABLET_LANDSCAPE
                    else -> DESKTOP
                }
            }
        }
    }
}