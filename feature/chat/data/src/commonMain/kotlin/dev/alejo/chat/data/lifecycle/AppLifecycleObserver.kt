package dev.alejo.chat.data.lifecycle

import kotlinx.coroutines.flow.Flow

expect class AppLifecycleObserver {
    val isInForeground: Flow<Boolean>
}