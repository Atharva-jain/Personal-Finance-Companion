package com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    fun savePreference(key: String, isEnabled: Boolean) {
        // FIXED: Using standard syntax instead of the 'edit {}' block
        prefs.edit { putBoolean(key, isEnabled) }
    }

    fun getPreference(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    // Keys
    companion object {
        const val KEY_MASTER_TOGGLE = "master_toggle"
        const val KEY_DAILY_REMINDER = "daily_reminder"
        const val KEY_WEEKLY_SUMMARY = "weekly_summary"
        const val KEY_GOAL_ALERTS = "goal_alerts"
        const val KEY_CHALLENGES = "challenges"
    }
}