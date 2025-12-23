package dev.alejo.chat.data.notification

import dev.alejo.chat.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService : PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}