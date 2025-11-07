package dev.alejo.auth.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.core.designsystem.components.brand.NexoSuccessIcon
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveResultLayout
import dev.alejo.core.designsystem.components.layout.NexoSimpleResultLayout
import dev.alejo.core.designsystem.components.layout.NexoScaffold
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.ObserveAsEvents
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.account_successfully_created
import nexo.feature.auth.presentation.generated.resources.email_verification_sent_to_x
import nexo.feature.auth.presentation.generated.resources.login
import nexo.feature.auth.presentation.generated.resources.resend_verification_email
import nexo.feature.auth.presentation.generated.resources.resent_verification_email
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel(),
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(
        viewModel.events
    ) { event ->
        when (event) {
            is RegisterSuccessEvent.ResendVerificationEmailSuccess -> {
                snackbarHostState.showSnackbar(
                    message = getString(
                        resource = Res.string.resent_verification_email
                    )
                )
            }
        }
    }

    RegisterSuccessScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterSuccessAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    NexoScaffold(
        snackbarHostState = snackbarHostState
    ) {
        NexoAdaptiveResultLayout {
            NexoSimpleResultLayout(
                title = stringResource(Res.string.account_successfully_created),
                description = stringResource(
                    Res.string.email_verification_sent_to_x,
                    state.registeredEmail
                ),
                icon = { NexoSuccessIcon() },
                primaryButton = {
                    NexoButton(
                        text = stringResource(Res.string.login),
                        onClick = {
                            onAction(RegisterSuccessAction.OnLoginClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                secondaryButton = {
                    NexoButton(
                        text = stringResource(Res.string.resend_verification_email),
                        onClick = {
                            onAction(RegisterSuccessAction.OnResendVerificationEmailClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !state.isResendingVerificationEmail,
                        isLoading = state.isResendingVerificationEmail,
                        style = NexoButtonStyle.SECONDARY
                    )
                },
                secondaryError = state.resendVerificationError?.asString()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "hi@jimmyplazas.dev"
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}