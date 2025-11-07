package dev.alejo.auth.presentation.forgot_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.core.designsystem.components.brand.NexoBrandLogo
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.layout.NexoAdaptiveFormLayout
import dev.alejo.core.designsystem.components.layout.NexoScaffold
import dev.alejo.core.designsystem.components.textfields.NexoTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.designsystem.theme.extended
import nexo.feature.auth.presentation.generated.resources.Res
import nexo.feature.auth.presentation.generated.resources.email
import nexo.feature.auth.presentation.generated.resources.email_placeholder
import nexo.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import nexo.feature.auth.presentation.generated.resources.forgot_password_title
import nexo.feature.auth.presentation.generated.resources.submit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    NexoScaffold {
        NexoAdaptiveFormLayout(
            headerText = stringResource(Res.string.forgot_password_title),
            errorText = state.error?.asString(),
            logo = { NexoBrandLogo() }
        ) {
            NexoTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                singleLine = true,
                supportingText = state.error?.asString(),
                keyboardType = KeyboardType.Email,
                title = stringResource(Res.string.email),
            )

            Spacer(modifier = Modifier.height(16.dp))

            NexoButton(
                text = stringResource(Res.string.submit),
                onClick = { onAction(ForgotPasswordAction.OnSubmitClick) },
                isLoading = state.isLoading,
                enabled = !state.isLoading && state.canSubmit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isEmailSentSuccessfully) {
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
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {}
        )
    }
}