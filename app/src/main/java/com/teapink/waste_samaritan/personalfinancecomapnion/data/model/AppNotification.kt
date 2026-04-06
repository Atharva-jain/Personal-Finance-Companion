package com.teapink.waste_samaritan.personalfinancecomapnion.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val timestampMillis: Long,
    val isRead: Boolean = false,
    val type: String // e.g., "REMINDER", "GOAL", "ALERT"
)