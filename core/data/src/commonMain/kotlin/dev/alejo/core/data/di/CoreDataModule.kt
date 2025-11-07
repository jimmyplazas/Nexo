package dev.alejo.core.data.di

import dev.alejo.core.data.auth.DataStoreSessionStorage
import dev.alejo.core.data.auth.KtorAuthService
import dev.alejo.core.data.logging.KermitLogger
import dev.alejo.core.data.networking.HttpClientFactory
import dev.alejo.core.domain.auth.AuthService
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.logging.NexoLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<NexoLogger> { KermitLogger }
    single {
        HttpClientFactory(get(), get())
            .create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class
}