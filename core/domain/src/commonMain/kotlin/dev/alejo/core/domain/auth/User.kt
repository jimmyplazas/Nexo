package dev.alejo.core.domain.auth

data class User(
    val id: String,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
    val profilePicture: String? = null,
)
