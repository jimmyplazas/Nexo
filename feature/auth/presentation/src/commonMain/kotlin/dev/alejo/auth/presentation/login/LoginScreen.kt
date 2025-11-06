package dev.alejo.auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveFormLayout
import dev.alejo.core.designsystem.components.textfields.NexoPasswordTextField
import dev.alejo.core.designsystem.components.textfields.NexoTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.ObserveAsEvents
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.create_account
import nexo.feature.auth.presentation.generated.resources.email
import nexo.feature.auth.presentation.generated.resources.email_placeholder
import nexo.feature.auth.presentation.generated.resources.forgot_password
import nexo.feature.auth.presentation.generated.resources.login
import nexo.feature.auth.presentation.generated.resources.password
import nexo.feature.auth.presentation.generated.resources.welcome_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                LoginAction.OnForgotPasswordClick -> onForgotPassword()
                LoginAction.OnSignUpClick -> onCreateAccount()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars
            .union(WindowInsets.displayCutout)
            .union(WindowInsets.ime)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NexoAdaptiveFormLayout(
                headerText = stringResource(Res.string.welcome_back),
                errorText = state.error?.asString(),
                logo = { NexoBrandLogo() }
            ) {

                NexoTextField(
                    state = state.emailTextFieldState,
                    placeholder = stringResource(Res.string.email_placeholder),
                    title = stringResource(Res.string.email),
                    keyboardType = KeyboardType.Email,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                NexoPasswordTextField(
                    state = state.passwordTextFieldState,
                    placeholder = stringResource(Res.string.password),
                    title = stringResource(Res.string.password),
                    isPasswordVisible = state.isPasswordVisible,
                    onToggleVisibilityClick = {
                        onAction(LoginAction.OnTogglePasswordVisibility)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.forgot_password),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .align(Alignment.End)
                        .clickable {
                            onAction(LoginAction.OnForgotPasswordClick)
                        }
                )

                Spacer(modifier = Modifier.height(32.dp))

                NexoButton(
                    text = stringResource(Res.string.login),
                    onClick = {
                        onAction(LoginAction.OnLoginClick)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canLogin,
                    isLoading = state.isLoggingIn
                )

                Spacer(modifier = Modifier.height(8.dp))

                NexoButton(
                    text = stringResource(Res.string.create_account),
                    onClick = {
                        onAction(LoginAction.OnSignUpClick)
                    },
                    style = NexoButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenErrorPreview() {
    NexoTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    NexoTheme(darkTheme = true) {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}