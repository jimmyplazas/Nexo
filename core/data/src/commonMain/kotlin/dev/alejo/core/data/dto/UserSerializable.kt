package dev.alejo.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSerializable(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("username") val username: String,
    @SerialName("hasEmailVerified") val hasVerifiedEmail: Boolean,
    @SerialName("profilePicture") val profilePicture: String? = null
)
