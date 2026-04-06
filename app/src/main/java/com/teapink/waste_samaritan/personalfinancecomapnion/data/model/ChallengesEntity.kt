package com.teapink.waste_samaritan.personalfinancecomapnion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val currentDays: Int,
    val targetDays: Int,
    val iconName: String,
    val colorArgb: Long
)