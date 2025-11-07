package dev.alejo.core.designsystem.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveFormLayout
import dev.alejo.core.designsystem.theme.NexoTheme

@PreviewScreenSizes
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