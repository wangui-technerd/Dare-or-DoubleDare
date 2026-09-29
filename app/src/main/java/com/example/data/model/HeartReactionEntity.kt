package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heart_reactions")
data class HeartReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dareLogId: Long,
    val gameSessionId: Long,
    val timestamp: Long = System.currentTimeMillis()
)
