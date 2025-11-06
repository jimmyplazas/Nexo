package dev.alejo.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.alejo.core.data.auth.createDataStore
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<DataStore<Preferences>> {
        createDataStore()
    }
}