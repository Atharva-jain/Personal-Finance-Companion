package com.teapink.waste_samaritan.personalfinancecomapnion.utils.bio_metric

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    fun saveAppLockState(isEnabled: Boolean) {
        prefs.edit { putBoolean(KEY_APP_LOCK, isEnabled) }
    }

    fun getAppLockState(defaultValue: Boolean = false): Boolean {
        // Defaults to false so the app isn't locked the very first time they install it
        return prefs.getBoolean(KEY_APP_LOCK, defaultValue)
    }

    companion object {
        const val KEY_APP_LOCK = "app_lock_enabled"
    }
}