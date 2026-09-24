package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dare_logs")
data class DareLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameSessionId: Long,
    val playerName: String,
    val cardText: String,
    val category: String,
    val level: Int,
    val outcome: String, // COMPLETED, BONUS, SKIPPED, NOT_COMFORTABLE, PARTIAL
    val points: Int,
    val timestamp: Long = System.currentTimeMillis()
)
