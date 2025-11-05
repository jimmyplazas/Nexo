package dev.alejo.auth.presentation.di

import dev.alejo.auth.presentation.register.RegisterViewModel
import dev.alejo.auth.presentation.register_success.RegisterSuccessViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
}