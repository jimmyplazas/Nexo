package dev.alejo.auth.presentation.di

import dev.alejo.auth.presentation.email_verification.EmailVerificationViewModel
import dev.alejo.auth.presentation.forgot_password.ForgotPasswordViewModel
import dev.alejo.auth.presentation.login.LoginViewModel
import dev.alejo.auth.presentation.register.RegisterViewModel
import dev.alejo.auth.presentation.register_success.RegisterSuccessViewModel
import dev.alejo.auth.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
}