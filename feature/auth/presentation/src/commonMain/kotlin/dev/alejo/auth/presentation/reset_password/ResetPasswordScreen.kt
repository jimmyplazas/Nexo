package dev.alejo.auth.presentation.reset_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveFormLayout
import dev.alejo.core.designsystem.components.layout.NexoScaffold
import dev.alejo.core.designsystem.components.textfields.NexoPasswordTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import nexo.feature.auth.presentation.generated.resources.new_password
import nexo.feature.auth.presentation.generated.resources.password
import nexo.feature.auth.presentation.generated.resources.password_hint
import nexo.feature.auth.presentation.generated.resources.set_new_password
import nexo.feature.auth.presentation.generated.resources.submit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    NexoScaffold {
        NexoAdaptiveFormLayout(
            headerText = stringResource(Res.string.set_new_password),
            errorText = state.errorText?.asString(),
            logo = { NexoBrandLogo() }
        ) {
            NexoPasswordTextField(
                state = state.passwordTextState,
                title = stringResource(Res.string.new_password),
                placeholder = stringResource(Res.string.password),
                supportingText = stringResource(Res.string.password_hint),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ResetPasswordAction.OnTogglePasswordVisibilityClick)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NexoButton(
                text = stringResource(Res.string.submit),
                onClick = {
                    onAction(ResetPasswordAction.OnSubmitClick)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.canSubmit && !state.isLoading,
                isLoading = state.isLoading
            )

            if (state.isResetSuccessful) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.forgot_password_email_sent_successfully),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}