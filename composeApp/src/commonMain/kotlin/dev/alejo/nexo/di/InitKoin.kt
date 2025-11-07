package dev.alejo.nexo.di

import dev.alejo.auth.presentation.di.authPresentationModule
import dev.alejo.chat.presentation.di.chatPresentationModule
import dev.alejo.core.data.di.coreDataModule
import dev.alejo.core.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            chatPresentationModule,
            corePresentationModule
        )
    }
}