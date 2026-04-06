package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model

import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.ChallengeRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.GoalRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.TransactionRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.theme.ThemePreferences
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.bio_metric.SecurityPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.CurrencyPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.dummy_data.DummyDataGenerator
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationHelper
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingViewModel(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val challengeRepository: ChallengeRepository,
    private val themePreferences: ThemePreferences,
    private val notificationPreferences: NotificationPreferences,
    private val currencyPreferences: CurrencyPreferences,
    private val securityPreferences: SecurityPreferences,
    val context: Context
) : ViewModel() {

    // Load initial state from preferences

    private val _masterNotifications =
        MutableStateFlow(notificationPreferences.getPreference(NotificationPreferences.KEY_MASTER_TOGGLE))
    val masterNotifications: StateFlow<Boolean> = _masterNotifications.asStateFlow()

    private val _dailyReminders =
        MutableStateFlow(notificationPreferences.getPreference(NotificationPreferences.KEY_DAILY_REMINDER))
    val dailyReminders: StateFlow<Boolean> = _dailyReminders.asStateFlow()

    private val _weeklySummaries =
        MutableStateFlow(notificationPreferences.getPreference(NotificationPreferences.KEY_WEEKLY_SUMMARY))
    val weeklySummaries: StateFlow<Boolean> = _weeklySummaries.asStateFlow()

    private val _goalAlerts =
        MutableStateFlow(notificationPreferences.getPreference(NotificationPreferences.KEY_GOAL_ALERTS))
    val goalAlerts: StateFlow<Boolean> = _goalAlerts.asStateFlow()

    private val _challenges =
        MutableStateFlow(notificationPreferences.getPreference(NotificationPreferences.KEY_CHALLENGES))
    val challenges: StateFlow<Boolean> = _challenges.asStateFlow()


    val isDarkMode: StateFlow<Boolean> = themePreferences.themeFlow

    private val _isNotificationsEnabled = MutableStateFlow(false)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    private val _isDataLoading = MutableStateFlow(false)
    val isDataLoading: StateFlow<Boolean> = _isDataLoading.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(currencyPreferences.getCurrencySymbol())
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _isAppLockEnabled = MutableStateFlow(securityPreferences.getAppLockState())
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled.asStateFlow()

    fun toggleAppLock(enabled: Boolean) {
        _isAppLockEnabled.value = enabled
        securityPreferences.saveAppLockState(enabled)
    }

    fun updateCurrency(symbol: String) {
        _selectedCurrency.value = symbol
        currencyPreferences.saveCurrencySymbol(symbol)
    }

    fun toggleDarkMode(enabled: Boolean) {
        themePreferences.saveTheme(enabled)
    }

    fun toggleMasterNotifications(context: Context, enabled: Boolean) {
        _masterNotifications.value = enabled
        notificationPreferences.savePreference(NotificationPreferences.KEY_MASTER_TOGGLE, enabled)

        if (!enabled) {
            // Kill everything if Master is turned off
            NotificationHelper.cancelAllNotifications(context)
        } else {
            // Restore active sub-categories if Master is turned back on
            if (_dailyReminders.value) NotificationHelper.scheduleDailyReminder(context)
            if (_weeklySummaries.value) NotificationHelper.scheduleWeeklySummary(context)
        }
    }

    fun toggleSpecificNotification(context: Context, key: String, enabled: Boolean) {
        notificationPreferences.savePreference(key, enabled)

        when (key) {
            NotificationPreferences.KEY_DAILY_REMINDER -> {
                _dailyReminders.value = enabled
                if (enabled && _masterNotifications.value) NotificationHelper.scheduleDailyReminder(
                    context
                )
                else NotificationHelper.cancelWork(context, NotificationHelper.WORK_DAILY_REMINDER)
            }

            NotificationPreferences.KEY_WEEKLY_SUMMARY -> {
                _weeklySummaries.value = enabled
                if (enabled && _masterNotifications.value) NotificationHelper.scheduleWeeklySummary(
                    context
                )
                else NotificationHelper.cancelWork(context, NotificationHelper.WORK_WEEKLY_SUMMARY)
            }
            // For Goal and Budget warnings, update the state here.
            // Usually, these are checked dynamically when saving a transaction!
            NotificationPreferences.KEY_GOAL_ALERTS -> _goalAlerts.value = enabled
            NotificationPreferences.KEY_CHALLENGES -> _challenges.value = enabled

        }
    }

    fun exportFullDatabase(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isDataLoading.value = true
            try {
                // 1. Fetch data from ALL tables (wait for the first non-loading state)
                val transResource =
                    transactionRepository.getAllTransactions().first { it !is Resources.Loading }
                val goalsResource = goalRepository.getAllGoals().first { it !is Resources.Loading }
                val chalResource =
                    challengeRepository.getAllChallenges().first { it !is Resources.Loading }

                val transactions = (transResource as? Resources.Success)?.data ?: emptyList()
                val goals = (goalsResource as? Resources.Success)?.data ?: emptyList()
                val challenges = (chalResource as? Resources.Success)?.data ?: emptyList()

                // 2. Build a unified CSV string categorized by sections
                val backupContent = buildString {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    // --- TRANSACTIONS SECTION ---
                    append("--- TRANSACTIONS ---\n")
                    append("ID,Date,Type,Category,Amount,Note\n")
                    transactions.forEach { t ->
                        val dateString = dateFormat.format(Date(t.dateMillis))
                        val cleanNote = t.note?.replace(",", " ") ?: "" // Prevent CSV breaks
                        append("${t.id},${dateString},${t.type},${t.category},${t.amount},${cleanNote}\n")
                    }
                    append("\n\n")

                    // --- GOALS SECTION ---
                    append("--- SAVINGS GOALS ---\n")
                    append("ID,Title,Target Amount,Saved Amount,Icon Name\n")
                    goals.forEach { g ->
                        val cleanTitle = g.title.replace(",", " ")
                        append("${g.id},${cleanTitle},${g.targetAmount},${g.savedAmount},${g.iconName}\n")
                    }
                    append("\n\n")

                    // --- CHALLENGES SECTION ---
                    append("--- CHALLENGES ---\n")
                    append("ID,Title,Current Days,Target Days,Icon Name\n")
                    challenges.forEach { c ->
                        val cleanTitle = c.title.replace(",", " ")
                        append("${c.id},${cleanTitle},${c.currentDays},${c.targetDays},${c.iconName}\n")
                    }
                }

                // 3. Write all data to the file URI provided by Android
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(backupContent.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isDataLoading.value = false
            }
        }
    }

    fun injectDummyData() {
        viewModelScope.launch {
            _isDataLoading.value = true
            delay(800)
            goalRepository.insertAllGoals(DummyDataGenerator.getDummyGoals())
            challengeRepository.insertAllChallenges(DummyDataGenerator.getDummyChallenges())
            transactionRepository.insertAllTransactions(DummyDataGenerator.getDummyTransactionsFor6Months())
            _isDataLoading.value = false
        }
    }
}

/*
fun toggleNotifications(context: Context, enabled: Boolean) {
        _isNotificationsEnabled.value = enabled
        // Save to preferences (assuming you have a Preferences class)
        // themePreferences.saveNotificationPref(enabled)

        // Actually schedule or cancel the background work!
        if (enabled) {
            NotificationHelper.scheduleDailyReminder(context)
        } else {
            NotificationHelper.cancelReminder(context)
        }

    }
* */