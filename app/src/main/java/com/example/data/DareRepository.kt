package com.example.data

import com.example.data.dao.DareCardDao
import com.example.data.dao.DareLogDao
import com.example.data.dao.GameSessionDao
import com.example.data.dao.HeartReactionDao
import com.example.data.dao.MomentHeartCount
import com.example.data.dao.MutedCardDao
import com.example.data.dao.UserDao
import com.example.data.model.DareCardEntity
import com.example.data.model.DareLogEntity
import com.example.data.model.GameSessionEntity
import com.example.data.model.HeartReactionEntity
import com.example.data.model.MutedCardEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DareRepository(
    private val userDao: UserDao,
    private val gameSessionDao: GameSessionDao,
    private val dareCardDao: DareCardDao,
    private val dareLogDao: DareLogDao,
    private val mutedCardDao: MutedCardDao,
    private val heartReactionDao: HeartReactionDao
) {
    constructor(database: AppDatabase) : this(
        database.userDao(),
        database.gameSessionDao(),
        database.dareCardDao(),
        database.dareLogDao(),
        database.mutedCardDao(),
        database.heartReactionDao()
    )

    // User / Auth operations
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getUserById(id: Long): Flow<UserEntity?> = userDao.getUserById(id)

    suspend fun getUserByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username)
    }

    suspend fun registerUser(user: UserEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getUserByUsername(user.username.trim().lowercase())
            if (existing != null) {
                Result.failure(Exception("Username already exists"))
            } else {
                val id = userDao.insertUser(user.copy(username = user.username.trim().lowercase()))
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun updateUserStats(userId: Long, games: Int, dares: Int, points: Int) = withContext(Dispatchers.IO) {
        userDao.incrementUserStats(userId, games, dares, points)
    }

    suspend fun deleteUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.deleteUser(user)
    }

    // Card operations
    val allCards: Flow<List<DareCardEntity>> = dareCardDao.getAllCards()
    val customCards: Flow<List<DareCardEntity>> = dareCardDao.getCustomCards()
    val favoriteCards: Flow<List<DareCardEntity>> = dareCardDao.getFavoriteCards()

    suspend fun getCardsByCategoryAndLevel(category: String, level: Int): List<DareCardEntity> = withContext(Dispatchers.IO) {
        dareCardDao.getCardsByCategoryAndLevel(category, level)
    }

    suspend fun getCardsByCategory(category: String): List<DareCardEntity> = withContext(Dispatchers.IO) {
        dareCardDao.getCardsByCategory(category)
    }

    suspend fun addCustomCard(card: DareCardEntity): Long = withContext(Dispatchers.IO) {
        dareCardDao.insertCard(card.copy(isCustom = true))
    }

    suspend fun toggleFavorite(cardId: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        dareCardDao.setFavorite(cardId, isFavorite)
    }

    suspend fun deleteCustomCard(cardId: Long) = withContext(Dispatchers.IO) {
        dareCardDao.deleteCustomCard(cardId)
    }

    suspend fun getCardById(cardId: Long): DareCardEntity? = withContext(Dispatchers.IO) {
        dareCardDao.getCardById(cardId)
    }

    suspend fun getCardsByIds(cardIds: List<Long>): List<DareCardEntity> = withContext(Dispatchers.IO) {
        dareCardDao.getCardsByIds(cardIds)
    }

    // Muted Card operations
    suspend fun muteCard(userId: Long, cardId: Long) = withContext(Dispatchers.IO) {
        mutedCardDao.mute(MutedCardEntity(userId = userId, cardId = cardId))
    }

    suspend fun getMutedCardIds(userId: Long): List<Long> = withContext(Dispatchers.IO) {
        mutedCardDao.mutedCardIds(userId)
    }

    suspend fun getAllMutedCardIds(): List<Long> = withContext(Dispatchers.IO) {
        mutedCardDao.allMutedCardIds()
    }

    suspend fun unmuteCard(userId: Long, cardId: Long) = withContext(Dispatchers.IO) {
        mutedCardDao.unmute(userId, cardId)
    }

    suspend fun unmuteCardForAll(cardId: Long) = withContext(Dispatchers.IO) {
        mutedCardDao.unmuteCardForAll(cardId)
    }

    suspend fun clearAllMutedCards() = withContext(Dispatchers.IO) {
        mutedCardDao.clearAll()
    }

    // Heart Reaction operations
    suspend fun giveHeart(dareLogId: Long, sessionId: Long) = withContext(Dispatchers.IO) {
        heartReactionDao.giveHeart(HeartReactionEntity(dareLogId = dareLogId, gameSessionId = sessionId))
    }

    suspend fun getTopMoments(sessionId: Long): List<MomentHeartCount> = withContext(Dispatchers.IO) {
        heartReactionDao.topMoments(sessionId)
    }

    suspend fun getHeartCountForLog(dareLogId: Long): Int = withContext(Dispatchers.IO) {
        heartReactionDao.getHeartCountForLog(dareLogId)
    }

    // Game Session operations
    val allSessions: Flow<List<GameSessionEntity>> = gameSessionDao.getAllSessions()

    suspend fun saveGameSession(session: GameSessionEntity): Long = withContext(Dispatchers.IO) {
        gameSessionDao.insertSession(session)
    }

    suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
        dareLogDao.deleteLogsForSession(sessionId)
        gameSessionDao.deleteSession(sessionId)
    }

    // Dare Log operations
    fun getLogsForSession(sessionId: Long): Flow<List<DareLogEntity>> = dareLogDao.getLogsForSession(sessionId)
    val recentLogs: Flow<List<DareLogEntity>> = dareLogDao.getRecentLogs()
    val allLogs: Flow<List<DareLogEntity>> = dareLogDao.getAllLogs()

    suspend fun logDare(log: DareLogEntity): Long = withContext(Dispatchers.IO) {
        dareLogDao.insertLog(log)
    }

    suspend fun clearAllLogs() = withContext(Dispatchers.IO) {
        dareLogDao.clearAllLogs()
    }
}
