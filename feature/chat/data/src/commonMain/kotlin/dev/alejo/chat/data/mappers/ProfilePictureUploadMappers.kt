package dev.alejo.chat.data.mappers

import dev.alejo.chat.data.dto.response.ProfilePictureUploadUrlsResponse
import dev.alejo.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}