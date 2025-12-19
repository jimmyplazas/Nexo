package dev.alejo.core.data.mappers

import dev.alejo.core.data.dto.AuthInfoSerializable
import dev.alejo.core.data.dto.UserSerializable
import dev.alejo.core.domain.auth.AuthInfo
import dev.alejo.core.domain.auth.User

fun AuthInfoSerializable.toDomain(profilePictureUrl: String? = null): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain(profilePictureUrl),
    )
}

fun UserSerializable.toDomain(profilePictureUrl: String? = null): User {
    return User(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        profilePicture = profilePictureUrl ?: profilePicture
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable(),
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        profilePicture = profilePicture
    )
}