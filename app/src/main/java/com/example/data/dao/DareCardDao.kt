package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DareCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DareCardDao {
    @Query("SELECT * FROM dare_cards ORDER BY level ASC, category ASC")
    fun getAllCards(): Flow<List<DareCardEntity>>

    @Query("SELECT * FROM dare_cards WHERE (UPPER(category) = UPPER(:category) OR REPLACE(UPPER(category), ' ', '_') = REPLACE(UPPER(:category), ' ', '_')) AND level = :level")
    suspend fun getCardsByCategoryAndLevel(category: String, level: Int): List<DareCardEntity>

    @Query("SELECT * FROM dare_cards WHERE UPPER(category) = UPPER(:category) OR REPLACE(UPPER(category), ' ', '_') = REPLACE(UPPER(:category), ' ', '_')")
    suspend fun getCardsByCategory(category: String): List<DareCardEntity>

    @Query("SELECT * FROM dare_cards WHERE isCustom = 1 ORDER BY id DESC")
    fun getCustomCards(): Flow<List<DareCardEntity>>

    @Query("SELECT * FROM dare_cards WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteCards(): Flow<List<DareCardEntity>>

    @Query("SELECT COUNT(*) FROM dare_cards")
    suspend fun getCardCount(): Int

    @Query("SELECT COUNT(*) FROM dare_cards WHERE isCustom = 0")
    suspend fun getDefaultCardCount(): Int

    @Query("SELECT COUNT(*) FROM dare_cards WHERE isCustom = 1")
    suspend fun getCustomCardCount(): Int

    @Query("SELECT * FROM dare_cards WHERE id = :id")
    suspend fun getCardById(id: Long): DareCardEntity?

    @Query("SELECT * FROM dare_cards WHERE id IN (:ids)")
    suspend fun getCardsByIds(ids: List<Long>): List<DareCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: DareCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<DareCardEntity>)

    @Query("UPDATE dare_cards SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM dare_cards WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomCard(id: Long)
}
