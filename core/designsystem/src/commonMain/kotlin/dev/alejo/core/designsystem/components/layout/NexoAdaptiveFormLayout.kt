package dev.alejo.core.designsystem.components.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import dev.alejo.core.presentation.util.DeviceConfiguration
import dev.alejo.core.presentation.util.clearFocusOnTap
import dev.alejo.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NexoAdaptiveFormLayout(
    headerText: String,
    errorText: String? = null,
    logo: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    formContent: @Composable ColumnScope.() -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val headerColor = if (configuration == DeviceConfiguration.MOBILE_LANDSCAPE) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.extended.textPrimary
    }

    when(configuration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            NexoSurface(
                modifier = modifier
                    .clearFocusOnTap()
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .consumeWindowInsets(WindowInsets.displayCutout),
                header = {
                    Spacer(modifier = Modifier.height(32.dp))
                    logo()
                    Spacer(modifier = Modifier.height(32.dp))
                }
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                AuthHeaderSection(
                    headerText = headerText,
                    headerColor = headerColor,
                    errorText = errorText,
                )
                Spacer(modifier = Modifier.height(24.dp))
                formContent()
            }
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
                    .clearFocusOnTap()
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .consumeWindowInsets(WindowInsets.displayCutout)
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    logo()
                    AuthHeaderSection(
                        headerText = headerText,
                        headerColor = headerColor,
                        errorText = errorText,
                        headerTextAlignment = TextAlign.Start
                    )
                }

                NexoSurface(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    formContent()
                }
            }
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Column(
                modifier = modifier
                    .clearFocusOnTap()
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                logo()
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AuthHeaderSection(
                        headerText = headerText,
                        headerColor = headerColor,
                        errorText = errorText
                    )
                    formContent()
                }
            }
        }
    }
}

@Composable
fun ColumnScope.AuthHeaderSection(
    headerText: String,
    headerColor: Color,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    headerTextAlignment: TextAlign = TextAlign.Center
) {
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleLarge,
        color = headerColor,
        textAlign = headerTextAlignment,
        modifier = modifier.fillMaxWidth()
    )
    AnimatedVisibility(
        visible = errorText != null
    ) {
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = headerTextAlignment
            )
        }
    }
}

@Preview
@Composable
fun NexoAdaptiveFormLayoutLightPreview() {
    NexoTheme {
        NexoAdaptiveFormLayout(
            headerText = "Welcome to Nexo",
            errorText = "Something went wrong",
            logo = {
                NexoBrandLogo()
            },
            formContent = {
                Text(
                    text = "This is a form",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        )
    }
}

@Preview
@Composable
fun NexoAdaptiveFormLayoutDarkPreview() {
    NexoTheme(darkTheme = true) {
        NexoAdaptiveFormLayout(
            headerText = "Welcome to Nexo",
            errorText = "Something went wrong",
            logo = {
                NexoBrandLogo()
            },
            formContent = {
                Text(
                    text = "This is a form",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        )
    }
}