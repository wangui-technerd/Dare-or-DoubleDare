package com.example.data.model

import androidx.room.Entity

@Entity(tableName = "muted_cards", primaryKeys = ["userId", "cardId"])
data class MutedCardEntity(
    val userId: Long,
    val cardId: Long,
    val mutedAt: Long = System.currentTimeMillis()
)
