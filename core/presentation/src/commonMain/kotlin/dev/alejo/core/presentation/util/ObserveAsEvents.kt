package dev.alejo.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * A composable function that collects a [Flow] of events and executes a callback for each event.
 * This is designed to handle one-off events like navigation, showing a snackbar, or displaying a dialog,
 * ensuring that the event is consumed only once.
 *
 * The collection is lifecycle-aware and will only occur when the composable is in the `STARTED`
 * state of its `Lifecycle`. The collection is automatically cancelled and restarted as the lifecycle
 * moves in and out of the `STARTED` state.
 *
 * The underlying `LaunchedEffect` will be relaunched if `key1` or `key2` change, allowing for
 * re-subscription to the flow if needed.
 *
 * @param T The type of the event emitted by the flow.
 * @param flow The [Flow] of events to observe.
 * @param key1 An optional key that, if changed, will cause the effect to be cancelled and re-launched.
 * @param key2 An optional second key that, if changed, will cause the effect to be cancelled and re-launched.
 * @param onEvent A lambda function to be executed for each event emitted by the [flow].
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}