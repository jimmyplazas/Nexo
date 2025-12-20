package dev.alejo.core.presentation.permissions

expect class PermissionController {
    suspend fun requestPermission(permission: Permission): PermissionState
}