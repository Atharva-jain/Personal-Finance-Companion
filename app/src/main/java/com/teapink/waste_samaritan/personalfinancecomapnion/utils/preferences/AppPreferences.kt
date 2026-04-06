package com.teapink.waste_samaritan.personalfinancecomapnion.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // --- Theme Settings ---
    fun saveTheme(isDark: Boolean) = prefs.edit { putBoolean("is_dark_mode", isDark) }
    fun isDarkMode(): Boolean = prefs.getBoolean("is_dark_mode", true)

    // --- Notification Settings ---
    fun saveNotificationEnabled(isEnabled: Boolean) = prefs.edit {
        putBoolean(
            "notifications_enabled",
            isEnabled
        )
    }
    fun isNotificationEnabled(): Boolean = prefs.getBoolean("notifications_enabled", false)

    // Save Hour (0-23) and Minute (0-59)
    fun saveReminderTime(hour: Int, minute: Int) {
        prefs.edit { putInt("reminder_hour", hour).putInt("reminder_minute", minute) }
    }

    // Default to 8:00 PM (20:00)
    fun getReminderHour(): Int = prefs.getInt("reminder_hour", 20)
    fun getReminderMinute(): Int = prefs.getInt("reminder_minute", 0)
}