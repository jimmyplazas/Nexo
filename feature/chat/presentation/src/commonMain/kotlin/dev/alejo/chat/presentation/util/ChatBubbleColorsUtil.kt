package dev.alejo.chat.presentation.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.alejo.core.designsystem.theme.extended
import kotlin.collections.listOf

@Composable
fun getChatBubbleColorForUser(userId: String): Color {
    val colorPool = with(MaterialTheme.colorScheme.extended) {
        listOf(
            cakeViolet,
            cakeGreen,
            cakePink,
            cakeOrange,
            cakeBlue,
            cakeYellow,
            cakePurple,
            cakeRed,
            cakeMint,
        )
    }

    val index = userId.hashCode().toUInt() % colorPool.size.toUInt()

    return colorPool[index.toInt()]
}