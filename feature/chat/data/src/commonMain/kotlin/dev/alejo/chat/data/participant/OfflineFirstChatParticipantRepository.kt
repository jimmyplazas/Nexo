package dev.alejo.chat.data.participant

import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.chat.domain.participant.ChatParticipantRepository
import dev.alejo.chat.domain.participant.ChatParticipantService
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.auth.SessionStorage
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.flow.first

class OfflineFirstChatParticipantRepository(
    private val sessionStorage: SessionStorage,
    private val chatParticipantService: ChatParticipantService
) : ChatParticipantRepository {

    override suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError> {
        return chatParticipantService
            .getLocalParticipant()
            .onSuccess { participant ->
                val currentAuthInfo = sessionStorage.observeAuthInf().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            id = participant.userId,
                            username = participant.username,
                            profilePicture = participant.profilePictureUrl
                        )
                    )
                )
            }
    }

    override suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote> {
        val result = chatParticipantService.getProfilePictureUploadUrl(mimeType)
        if (result is Result.Failure) {
            return result
        }

        val uploadUrls = (result as Result.Success).data
        val uploadResult = chatParticipantService.uploadProfilePicture(
            uploadUrl = uploadUrls.uploadUrl,
            imageBytes = imageBytes,
            headers = uploadUrls.headers
        )

        if (uploadResult is Result.Failure) {
            return uploadResult
        }

        return chatParticipantService
            .confirmProfilePictureUpload(uploadUrls.publicUrl)
            .onSuccess {
                val currentAuthInfo = sessionStorage.observeAuthInf().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            profilePicture = uploadUrls.publicUrl
                        )
                    )
                )
            }
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return chatParticipantService
            .deleteProfilePicture()
            .onSuccess {
                val currentAuthInfo = sessionStorage.observeAuthInf().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            profilePicture = null
                        )
                    )
                )
            }
    }
}