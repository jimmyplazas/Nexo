package dev.alejo.chat.presentation.profile.mediapicker

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (PickedImageData) -> Unit
): ImagePickerLauncher

class ImagePickerLauncher(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}

class PickedImageData(
    val bytes: ByteArray,
    val mimeType: String?
)