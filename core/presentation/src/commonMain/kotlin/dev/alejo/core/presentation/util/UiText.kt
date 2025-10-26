package dev.alejo.core.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * A sealed interface representing text that can be displayed in the UI.
 * This is a wrapper to handle both dynamic strings and string resources
 * in a unified way, especially within ViewModels or business logic where
 * a direct Android/Compose context is not available.
 *
 * It allows defining UI text in a context-agnostic manner and resolving it
 * to a concrete `String` only when needed, either in a Composable function
 * or a suspend function.
 */
sealed interface UiText {
    /**
     * Represents a piece of text that is determined at runtime, such as user input or
     * data fetched from an API. It holds a plain [String] value.
     *
     * @param value The actual string content to be displayed.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Represents a piece of text that comes from a string resource.
     *
     * This is useful for providing localized and formatted strings from your `composeResources`.
     *
     * @property id The [StringResource] identifier for the string.
     * @property args Optional varargs for formatting the string resource.
     */
    class Resource(
        val id: StringResource,
        val args: Array<Any> = emptyArray()
    ) : UiText

    /**
     * Resolves the `UiText` into a `String`.
     *
     * This function is a `@Composable` function and should be called from within a Composable context.
     * It handles both `DynamicString` and `Resource` types.
     * - For `DynamicString`, it returns the raw string value.
     * - For `Resource`, it uses `stringResource` to resolve the string resource ID with its arguments.
     *
     * @return The resolved string.
     */
    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> stringResource(id, *args)
        }
    }

    /**
     * Resolves the [UiText] to a plain [String] asynchronously.
     *
     * This function is intended for use in non-Composable scopes, such as ViewModels or background tasks.
     * It handles both [DynamicString] and [Resource] types.
     *
     * @return The resolved [String]. For [DynamicString], it returns the raw value. For [Resource],
     * it fetches the string resource using the provided ID and formats it with any arguments.
     */
    suspend fun asStringAsync(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> getString(id, *args)
        }
    }
}