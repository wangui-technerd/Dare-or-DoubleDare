package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val pinHash: String,
    val displayName: String,
    val avatarEmoji: String = "💋",
    val avatarColorHex: String = "#C92A45",
    val safeWord: String = "Pineapple",
    val intensityPreference: Int = 2, // 1 to 4
    val gamesPlayed: Int = 0,
    val daresCompleted: Int = 0,
    val totalPoints: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
