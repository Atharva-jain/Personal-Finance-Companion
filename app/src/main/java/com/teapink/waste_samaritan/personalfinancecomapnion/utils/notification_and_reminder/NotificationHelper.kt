package com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationHelper {
    const val WORK_DAILY_REMINDER = "daily_finance_reminder"
    const val WORK_WEEKLY_SUMMARY = "weekly_finance_summary"
    const val WORK_GOAL_ALERTS = "goal_alerts_checker"
    const val WORK_CHALLENGES = "challenges_checker"

    fun scheduleDailyReminder(context: Context, hour: Int = 20, minute: Int = 0) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        val initialDelay = target.timeInMillis - now.timeInMillis

        // Pass the specific text for a DAILY reminder
        val inputData = Data.Builder()
            .putString("title", "Time for a Wallet Check! 💸")
            .putString("message", "Did you spend any money today? Log it now to keep your goals on track.")
            .putString("type", "REMINDER")
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData) // <--- Injecting Data here
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_DAILY_REMINDER, ExistingPeriodicWorkPolicy.UPDATE, workRequest
        )
    }

    fun scheduleWeeklySummary(context: Context, dayOfWeek: Int = Calendar.SUNDAY, hour: Int = 9, minute: Int = 0) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) target.add(Calendar.WEEK_OF_YEAR, 1)
        val initialDelay = target.timeInMillis - now.timeInMillis

        // Pass the specific text for a WEEKLY summary
        val inputData = Data.Builder()
            .putString("title", "Weekly Financial Summary 📊")
            .putString("message", "Your spending summary for this week is ready. Tap to review your progress.")
            .putString("type", "SUMMARY")
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData) // <--- Injecting Data here
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_WEEKLY_SUMMARY, ExistingPeriodicWorkPolicy.UPDATE, workRequest
        )
    }

    fun sendTestNotificationNow(context: Context) {
        val inputData = Data.Builder()
            .putString("title", "Test Notification 🚀")
            .putString("message", "Your notification system is working perfectly!")
            .putString("type", "REMINDER")
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelWork(context: Context, workName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }

    fun cancelAllNotifications(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_DAILY_REMINDER)
        workManager.cancelUniqueWork(WORK_WEEKLY_SUMMARY)
        workManager.cancelUniqueWork(WORK_GOAL_ALERTS)
        workManager.cancelUniqueWork(WORK_CHALLENGES)
    }

    fun sendAllTestNotificationsNow(context: Context) {
        // We will trigger 4 separate workers, one for each type
        val notificationTypes = listOf("REMINDER", "SUMMARY", "CHALLENGE", "GOAL")
        val workManager = WorkManager.getInstance(context)

        notificationTypes.forEach { type ->
            val inputData = Data.Builder()
                .putString("type", type)
                .putBoolean("force_show", true) // Ensures the test button always generates a pop-up
                .build()

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(workRequest)
        }
    }

}