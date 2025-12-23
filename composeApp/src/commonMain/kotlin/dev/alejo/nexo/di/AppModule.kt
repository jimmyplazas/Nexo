package dev.alejo.nexo.di

import dev.alejo.nexo.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}