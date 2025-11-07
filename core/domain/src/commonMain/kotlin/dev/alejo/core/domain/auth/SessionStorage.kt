package dev.alejo.core.domain.auth

import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    fun observeAuthInf(): Flow<AuthInfo?>
    suspend fun set(info: AuthInfo?)
}