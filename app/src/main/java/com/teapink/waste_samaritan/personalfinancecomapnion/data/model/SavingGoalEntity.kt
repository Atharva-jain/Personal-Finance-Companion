package com.teapink.waste_samaritan.personalfinancecomapnion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val iconName: String, // e.g., "Laptop", "Flight", "Health"
    val colorArgb: Long   // e.g., 0xFF2962FF
)