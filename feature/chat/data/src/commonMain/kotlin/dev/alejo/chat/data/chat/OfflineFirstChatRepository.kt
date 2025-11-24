package dev.alejo.chat.data.chat

import dev.alejo.chat.data.mappers.toDomain
import dev.alejo.chat.data.mappers.toEntity
import dev.alejo.chat.data.mappers.toLastMessageView
import dev.alejo.chat.database.NexoChatDatabase
import dev.alejo.chat.database.entities.ChatInfoEntity
import dev.alejo.chat.database.entities.ChatParticipantEntity
import dev.alejo.chat.database.entities.ChatWithParticipants
import dev.alejo.chat.domain.chat.ChatRepository
import dev.alejo.chat.domain.chat.ChatService
import dev.alejo.chat.domain.models.Chat
import dev.alejo.chat.domain.models.ChatInfo
import dev.alejo.core.domain.EmptyResult
import dev.alejo.core.domain.Result
import dev.alejo.core.domain.asEmptyResult
import dev.alejo.core.domain.onFailure
import dev.alejo.core.domain.onSuccess
import dev.alejo.core.domain.util.DataError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope

class OfflineFirstChatRepository(
    private val chatService: ChatService,
    private val db: NexoChatDatabase
) : ChatRepository {

    override fun getChats(): Flow<List<Chat>> {
        return db.chatDao.getChatsWithParticipants()
            .map { allChatWithParticipants ->
                supervisorScope {
                    allChatWithParticipants
                        .map { chatWithParticipants ->
                            async {
                                ChatWithParticipants(
                                    chat = chatWithParticipants.chat,
                                    participants = chatWithParticipants
                                        .participants
                                        .onlyActive(chatWithParticipants.chat.chatId),
                                    lastMessage = chatWithParticipants.lastMessage
                                )
                            }
                        }
                        .awaitAll()
                        .map { it.toDomain() }
                }
            }
    }

    override fun getChatInfoById(chatId: String): Flow<ChatInfo> {
        return db.chatDao.getChatInfoById(chatId)
            .filterNotNull()
            .map {  chatInfo ->
                ChatInfoEntity(
                    chat = chatInfo.chat,
                    participants = chatInfo
                        .participants
                        .onlyActive(chatInfo.chat.chatId),
                    messagesWithSenders = chatInfo.messagesWithSenders
                )
            }
            .map { it.toDomain() }
    }

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        return chatService.getChats()
            .onSuccess { chats ->
                val chatWithParticipants = chats.map { chat ->
                    ChatWithParticipants(
                        chat = chat.toEntity(),
                        participants = chat.participants.map { it.toEntity() },
                        lastMessage = chat.lastMessage?.toLastMessageView()
                    )
                }
                db.chatDao.upsertChatsWithParticipantsAndCrossRefs(
                    chats = chatWithParticipants,
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantsCrossRefDao,
                    messageDao = db.chatMessageDao
                )
            }
            .onFailure { error ->

            }
    }

    override suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote> {
        return chatService
            .getChatById(chatId)
            .onSuccess { chat ->
                db.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantsCrossRefDao
                )
            }
            .asEmptyResult()
    }

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return chatService
            .createChat(otherUserIds)
            .onSuccess { chat ->
                db.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantsCrossRefDao
                )
            }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return chatService
            .leaveCHat(chatId)
            .onSuccess {
                db.chatDao.deleteChatById(chatId)
            }
    }

    private suspend fun List<ChatParticipantEntity>.onlyActive(chatId: String): List<ChatParticipantEntity> {
        val activeParticipantIds = db
            .chatDao
            .getActiveParticipantsByChatId(chatId)
            .first()
            .map { it.userId }

        return this.filter { it.userId in activeParticipantIds }
    }
}