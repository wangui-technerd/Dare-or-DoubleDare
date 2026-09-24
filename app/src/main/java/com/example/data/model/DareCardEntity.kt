package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dare_cards")
data class DareCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // POWER, SENSATION, ROLEPLAY, PRAISE, WILDCARD
    val level: Int,       // 1 (Tease), 2 (Tempt), 3 (Heat), 4 (After Dark)
    val text: String,
    val scope: String,    // solo, paired, group
    val timerSeconds: Int? = null,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val createdByUserId: Long? = null
)
