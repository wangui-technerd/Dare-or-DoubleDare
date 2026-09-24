package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GameSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): GameSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity): Long

    @Query("DELETE FROM game_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}
