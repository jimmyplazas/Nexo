package dev.alejo.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.alejo.chat.presentation.chat_list_detail.ChatListDetailAdaptiveLayout
import kotlinx.serialization.Serializable

sealed interface ChatGraphRoutes {
    @Serializable
    data object Graph : ChatGraphRoutes

    @Serializable
    data class ChatListDetailRoute(val chatId: String? = null) : ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatListDetailRoute()
    ) {
        composable<ChatGraphRoutes.ChatListDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "nexo://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.toRoute<ChatGraphRoutes.ChatListDetailRoute>().chatId

            ChatListDetailAdaptiveLayout(
                initialChatId = chatId,
                onLogout = onLogout
            )
        }
    }
}