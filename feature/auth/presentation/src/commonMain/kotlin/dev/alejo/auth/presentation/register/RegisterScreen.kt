package dev.alejo.auth.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveFormLayout
import dev.alejo.core.designsystem.components.layout.NexoScaffold
import dev.alejo.core.designsystem.components.textfields.NexoPasswordTextField
import dev.alejo.core.designsystem.components.textfields.NexoTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.ObserveAsEvents
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.email
import nexo.feature.auth.presentation.generated.resources.email_placeholder
import nexo.feature.auth.presentation.generated.resources.login
import nexo.feature.auth.presentation.generated.resources.password
import nexo.feature.auth.presentation.generated.resources.password_hint
import nexo.feature.auth.presentation.generated.resources.register
import nexo.feature.auth.presentation.generated.resources.username
import nexo.feature.auth.presentation.generated.resources.username_hint
import nexo.feature.auth.presentation.generated.resources.username_placeholder
import nexo.feature.auth.presentation.generated.resources.welcome_to_nexo
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.Success -> onRegisterSuccess(event.email)
        }
    }

    RegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    NexoScaffold(
        snackbarHostState = snackbarHostState,
        content = {
            NexoAdaptiveFormLayout(
                headerText = stringResource(Res.string.welcome_to_nexo),
                errorText = state.registrationError?.asString(),
                logo = { NexoBrandLogo() }
            ) {
                NexoTextField(
                    state = state.usernameTextState,
                    placeholder = stringResource(Res.string.username_placeholder),
                    title = stringResource(Res.string.username),
                    supportingText = state.usernameError?.asString()
                        ?: stringResource(Res.string.username_hint),
                    isError = state.usernameError != null,
                    singleLine = true,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                NexoTextField(
                    state = state.emailTextState,
                    placeholder = stringResource(Res.string.email_placeholder),
                    title = stringResource(Res.string.email),
                    supportingText = state.emailError?.asString(),
                    isError = state.emailError != null,
                    singleLine = true,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                NexoPasswordTextField(
                    state = state.passwordTextState,
                    placeholder = stringResource(Res.string.password),
                    title = stringResource(Res.string.password),
                    supportingText = state.passwordError?.asString()
                        ?: stringResource(Res.string.password_hint),
                    isError = state.passwordError != null,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    },
                    isPasswordVisible = state.isPasswordVisible,
                    onToggleVisibilityClick = {
                        onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                NexoButton(
                    text = stringResource(Res.string.register),
                    onClick = {
                        onAction(RegisterAction.OnRegisterClick)
                    },
                    enabled = state.canRegister,
                    isLoading = state.isRegistering,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NexoButton(
                    text = stringResource(Res.string. login),
                    onClick = {
                        onAction(RegisterAction.OnLoginClick)
                    },
                    style = NexoButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}