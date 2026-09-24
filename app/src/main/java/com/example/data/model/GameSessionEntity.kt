package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val winnerName: String,
    val winnerPoints: Int,
    val playersJson: String, // serialized player summaries
    val roundsCount: Int,
    val daresCompletedCount: Int,
    val safeWordUsed: Boolean = false
)
