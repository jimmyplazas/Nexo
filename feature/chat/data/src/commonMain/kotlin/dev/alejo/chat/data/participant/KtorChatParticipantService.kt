package dev.alejo.chat.data.participant

import dev.alejo.chat.data.dto.ChatParticipantDto
import dev.alejo.chat.data.dto.request.ConfirmProfilePictureRequest
import dev.alejo.chat.data.dto.response.ProfilePictureUploadUrlsResponse
import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.domain.models.ChatParticipant
import dev.alejo.chat.domain.models.ProfilePictureUploadUrls
import dev.alejo.chat.domain.participant.ChatParticipantService
import dev.alejo.core.data.networking.delete
import dev.alejo.core.data.networking.get
import dev.alejo.core.data.networking.post
import dev.alejo.core.data.networking.safeCall
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.map
import dev.alejo.core.domain.util.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class KtorChatParticipantService(
    private val httpClient: HttpClient
) : ChatParticipantService {
    override suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        ).map { it.toDomain() }
    }

    override suspend fun getLocalParticipant(): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants"
        ).map { it.toDomain() }
    }

    override suspend fun getProfilePictureUploadUrl(
        mimeType: String
    ): Result<ProfilePictureUploadUrls, DataError.Remote> {
        return httpClient.post<Unit, ProfilePictureUploadUrlsResponse>(
            route = "/participants/profile-picture-upload",
            queryParams = mapOf(
                "mimeType" to mimeType
            ),
            body = Unit
        ).map { it.toDomain() }
    }

    override suspend fun uploadProfilePicture(
        uploadUrl: String,
        imageBytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote> {
        return safeCall {
            httpClient.put {
                url(uploadUrl)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(imageBytes)
            }
        }
    }

    override suspend fun confirmProfilePictureUpload(
        publicUrl: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post<ConfirmProfilePictureRequest, Unit>(
            route = "/participants/confirm-picture-upload",
            body = ConfirmProfilePictureRequest(publicUrl)
        )
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/participants/profile-picture"
        )
    }
}