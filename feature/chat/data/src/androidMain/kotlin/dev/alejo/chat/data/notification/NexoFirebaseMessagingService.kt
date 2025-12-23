package dev.alejo.chat.data.notification

import com.google.firebase.messaging.FirebaseMessagingService
import dev.alejo.chat.domain.notification.DeviceTokenService
import dev.alejo.core.domain.auth.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NexoFirebaseMessagingService : FirebaseMessagingService() {

    private val deviceTokenService by inject<DeviceTokenService>()
    private val sessionStorage by inject<SessionStorage>()
    private val applicationScop by inject<CoroutineScope>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        applicationScop.launch {
            val authInfo = sessionStorage.observeAuthInf().first()
            if (authInfo != null) {
                deviceTokenService
                    .registerToken(
                        token = token,
                        platform = "ANDROID"
                    )
            }

        }
    }
}