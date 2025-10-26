package dev.alejo.core.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class Resource(
        val id: StringResource,
        val args: Array<Any> = emptyArray()
    ) : UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> stringResource(id, *args)
        }
    }

    suspend fun asStringAsync(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> getString(id, *args)
        }
    }
}