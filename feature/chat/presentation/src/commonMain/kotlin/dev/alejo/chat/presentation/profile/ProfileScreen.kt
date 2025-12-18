package dev.alejo.chat.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alejo.chat.presentation.profile.components.ProfileHeaderSection
import dev.alejo.chat.presentation.profile.components.ProfileSectionLayout
import dev.alejo.core.designsystem.components.avatar.AvatarSize
import dev.alejo.core.designsystem.components.avatar.NexoAvatarPhoto
import dev.alejo.core.designsystem.components.brand.NexoHorizontalDivider
import dev.alejo.core.designsystem.components.buttons.NexoButton
import dev.alejo.core.designsystem.components.buttons.NexoButtonStyle
import dev.alejo.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import dev.alejo.core.designsystem.components.dialogs.NexoAdaptiveDialogSheetLayout
import dev.alejo.core.designsystem.components.textfields.NexoPasswordTextField
import dev.alejo.core.designsystem.components.textfields.NexoTextField
import dev.alejo.core.designsystem.theme.NexoTheme
import dev.alejo.core.presentation.util.clearFocusOnTap
import dev.alejo.core.presentation.util.currentDeviceConfiguration
import nexo.feature.chat.presentation.generated.resources.Res
import nexo.feature.chat.presentation.generated.resources.cancel
import nexo.feature.chat.presentation.generated.resources.contact_nexo_support_change_email
import nexo.feature.chat.presentation.generated.resources.current_password
import nexo.feature.chat.presentation.generated.resources.delete
import nexo.feature.chat.presentation.generated.resources.delete_profile_picture
import nexo.feature.chat.presentation.generated.resources.delete_profile_picture_desc
import nexo.feature.chat.presentation.generated.resources.email
import nexo.feature.chat.presentation.generated.resources.new_password
import nexo.feature.chat.presentation.generated.resources.password
import nexo.feature.chat.presentation.generated.resources.password_hint
import nexo.feature.chat.presentation.generated.resources.profile_picture
import nexo.feature.chat.presentation.generated.resources.save
import nexo.feature.chat.presentation.generated.resources.upload_icon
import nexo.feature.chat.presentation.generated.resources.upload_picture
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NexoAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ProfileScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is ProfileAction.OnDismiss -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderSection(
            username = state.username,
            onCloseClick = {
                onAction(ProfileAction.OnDismiss)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 20.dp
                )
        )
        NexoHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.profile_picture)
        ) {
            Row {
                NexoAvatarPhoto(
                    displayText = state.userInitials,
                    size = AvatarSize.LARGE,
                    imageUrl = state.profilePictureUrl,
                    onClick = {
                        onAction(ProfileAction.OnUploadPictureClick)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NexoButton(
                        text = stringResource(Res.string.upload_picture),
                        onClick = {
                            onAction(ProfileAction.OnUploadPictureClick)
                        },
                        style = NexoButtonStyle.SECONDARY,
                        enabled = !state.isUploadingImage && !state.isDeletingImage,
                        isLoading = state.isUploadingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.upload_icon),
                                contentDescription = stringResource(Res.string.upload_picture)
                            )
                        }
                    )
                    NexoButton(
                        text = stringResource(Res.string.delete),
                        onClick = {
                            onAction(ProfileAction.OnDeletePictureClick)
                        },
                        style = NexoButtonStyle.DESTRUCTIVE_SECONDARY,
                        enabled = !state.isUploadingImage &&
                                !state.isDeletingImage &&
                                state.profilePictureUrl != null,
                        isLoading = state.isDeletingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.delete)
                            )
                        }
                    )
                }
            }

            if (state.imageError != null) {
                Text(
                    text = state.imageError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        NexoHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.email)
        ) {
            NexoTextField(
                state = state.emailTextState,
                enabled = false,
                supportingText = stringResource(Res.string.contact_nexo_support_change_email)
            )
        }
        NexoHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.password)
        ) {
            NexoPasswordTextField(
                state = state.currentPasswordTextState,
                isPasswordVisible = state.isCurrentPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleCurrentPasswordVisibility)
                },
                placeholder = stringResource(Res.string.current_password),
                supportingText = state.currentPasswordError?.asString(),
                isError = state.currentPasswordError != null
            )
            NexoPasswordTextField(
                state = state.newPasswordTextState,
                isPasswordVisible = state.isNewPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleNewPasswordVisibility)
                },
                placeholder = stringResource(Res.string.new_password),
                supportingText = state.newPasswordError?.asString()
                    ?: stringResource(Res.string.password_hint),
                isError = state.newPasswordError != null
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                NexoButton(
                    text = stringResource(Res.string.cancel),
                    style = NexoButtonStyle.SECONDARY,
                    onClick = {
                        onAction(ProfileAction.OnDismiss)
                    }
                )
                NexoButton(
                    text = stringResource(Res.string.save),
                    enabled = state.canChangePassword,
                    isLoading = state.isChangingPassword,
                    onClick = {
                        onAction(ProfileAction.OnChangePasswordClick)
                    }
                )
            }
        }

        val deviceConfiguration = currentDeviceConfiguration()
        if (deviceConfiguration.isMobile) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (state.showDeleteConfirmationDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_profile_picture),
            description = stringResource(Res.string.delete_profile_picture_desc),
            confirmButtonText = stringResource(Res.string.delete),
            cancelButtonText = stringResource(Res.string.cancel),
            onDismiss = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            },
            onConfirm = {
                onAction(ProfileAction.OnConfirmDeleteClick)
            },
            onCancel = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    NexoTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {}
        )
    }
}