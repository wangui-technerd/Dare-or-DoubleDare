package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DareLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DareLogDao {
    @Query("SELECT * FROM dare_logs WHERE gameSessionId = :sessionId ORDER BY timestamp DESC")
    fun getLogsForSession(sessionId: Long): Flow<List<DareLogEntity>>

    @Query("SELECT * FROM dare_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<DareLogEntity>>

    @Query("SELECT * FROM dare_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<DareLogEntity>>

    @Query("SELECT * FROM dare_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsList(): List<DareLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DareLogEntity): Long

    @Query("DELETE FROM dare_logs WHERE gameSessionId = :sessionId")
    suspend fun deleteLogsForSession(sessionId: Long)

    @Query("DELETE FROM dare_logs")
    suspend fun clearAllLogs()
}
