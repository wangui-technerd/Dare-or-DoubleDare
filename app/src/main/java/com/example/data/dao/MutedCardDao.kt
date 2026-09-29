package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MutedCardEntity

@Dao
interface MutedCardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun mute(entity: MutedCardEntity)

    @Query("SELECT cardId FROM muted_cards WHERE userId = :userId")
    suspend fun mutedCardIds(userId: Long): List<Long>

    @Query("SELECT cardId FROM muted_cards")
    suspend fun allMutedCardIds(): List<Long>

    @Query("DELETE FROM muted_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun unmute(userId: Long, cardId: Long)

    @Query("DELETE FROM muted_cards WHERE cardId = :cardId")
    suspend fun unmuteCardForAll(cardId: Long)

    @Query("DELETE FROM muted_cards")
    suspend fun clearAll()
}
