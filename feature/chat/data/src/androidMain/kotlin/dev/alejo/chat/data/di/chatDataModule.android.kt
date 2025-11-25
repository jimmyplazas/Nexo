package dev.alejo.chat.data.di

import dev.alejo.chat.data.lifecycle.AppLifecycleObserver
import dev.alejo.chat.data.network.ConnectivityObserver
import dev.alejo.chat.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory(androidContext()) }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
}