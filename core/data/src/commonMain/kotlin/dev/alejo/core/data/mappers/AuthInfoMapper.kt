package dev.alejo.core.data.mappers

import dev.alejo.core.data.dto.AuthInfoSerializable
import dev.alejo.core.data.dto.UserSerializable
import dev.alejo.core.domain.auth.AuthInfo
import dev.alejo.core.domain.auth.User

fun AuthInfoSerializable.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain(),
    )
}

fun UserSerializable.toDomain(): User {
    return User(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        profilePicture = profilePicture
    )
}