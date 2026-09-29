package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.HeartReactionEntity

data class MomentHeartCount(
    val dareLogId: Long,
    val heartCount: Int
)

data class TopMomentDetail(
    val dareLogId: Long,
    val playerName: String,
    val cardText: String,
    val category: String,
    val heartCount: Int
)

@Dao
interface HeartReactionDao {
    @Insert
    suspend fun giveHeart(entity: HeartReactionEntity)

    @Query("""
        SELECT dareLogId, COUNT(*) as heartCount 
        FROM heart_reactions 
        WHERE gameSessionId = :sessionId 
        GROUP BY dareLogId 
        ORDER BY heartCount DESC
    """)
    suspend fun topMoments(sessionId: Long): List<MomentHeartCount>

    @Query("SELECT COUNT(*) FROM heart_reactions WHERE dareLogId = :dareLogId")
    suspend fun getHeartCountForLog(dareLogId: Long): Int
}
