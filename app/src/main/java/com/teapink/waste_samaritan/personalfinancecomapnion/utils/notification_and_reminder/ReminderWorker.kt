package com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder

import com.teapink.waste_samaritan.personalfinancecomapnion.data.databases.PersonalFinanceCompanionDatabase
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import java.util.Calendar


class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val type = inputData.getString("type") ?: "REMINDER"
            val forceShow = inputData.getBoolean("force_show", false)

            var finalTitle = "Finance Companion"
            var finalMessage = "You have a new alert."
            var shouldShowNotification = true

            // Connect to the REAL database
            val database = PersonalFinanceCompanionDatabase.getInstance(context.applicationContext)
            val now = Calendar.getInstance()

            when (type) {
                "REMINDER" -> {
                    // Check today's actual transactions
                    val startOfDay = getStartOfDay(now).timeInMillis
                    val endOfDay = getEndOfDay(now).timeInMillis
                    val todaysTransactions = database.transactionDao().getTransactionsForDateRange(startOfDay, endOfDay)

                    if (todaysTransactions.isEmpty()) {
                        finalTitle = "Time for a Wallet Check! 💸"
                        finalMessage = "You haven't logged any expenses today. Did you spend anything?"
                    } else {
                        finalTitle = "Great Job Today! 🌟"
                        finalMessage = "You've logged ${todaysTransactions.size} transactions today. Keep up the good habit!"
                        // In normal background mode, we don't send a notification if they already logged data.
                        if (!forceShow) shouldShowNotification = false
                    }
                }

                "SUMMARY" -> {
                    // Calculate real expenses for the last 7 days
                    val endOfWeek = now.timeInMillis
                    now.add(Calendar.DAY_OF_YEAR, -7)
                    val startOfWeek = getStartOfDay(now).timeInMillis

                    val weeklySpent = database.transactionDao().getExpenseSumForDateRange(startOfWeek, endOfWeek) ?: 0.0

                    finalTitle = "Weekly Financial Summary 📊"
                    finalMessage = "You spent ₹${String.format("%.0f", weeklySpent)} this past week. Tap to review your progress."
                }

                "CHALLENGE" -> {
                    // Fetch real challenges
                    val challenges = database.challengeDao().getAllChallengesSync()
                    val activeChallenge = challenges.firstOrNull { it.currentDays < it.targetDays }

                    if (activeChallenge != null) {
                        finalTitle = "Keep up the ${activeChallenge.title}! 🌟"
                        finalMessage = "You are on day ${activeChallenge.currentDays} of ${activeChallenge.targetDays}. Keep going!"
                    } else {
                        finalTitle = "Ready for a Challenge? 🚀"
                        finalMessage = "You have no active challenges. Start one today to boost your savings!"
                        if (!forceShow) shouldShowNotification = false
                    }
                }

                "GOAL" -> {
                    // Fetch real goals
                    val goals = database.goalDoa().getAllGoalsSync()
                    // Find the goal closest to completion
                    val activeGoal = goals.maxByOrNull { if (it.targetAmount > 0) it.savedAmount / it.targetAmount else 0.0 }

                    if (activeGoal != null) {
                        val percentage = ((activeGoal.savedAmount / activeGoal.targetAmount) * 100).toInt()
                        finalTitle = "Goal Progress: ${activeGoal.title} 🏆"
                        finalMessage = "You are $percentage% of the way to your goal! You've saved ₹${String.format("%.0f", activeGoal.savedAmount)} so far."
                    } else {
                        finalTitle = "Set a Savings Goal! 🎯"
                        finalMessage = "You don't have any active savings goals. Set one up to track your progress."
                        if (!forceShow) shouldShowNotification = false
                    }
                }
            }

            // Fire the notification if conditions are met (or if it's a manual test)
            if (shouldShowNotification || forceShow) {
                showSystemNotification(finalTitle, finalMessage, type)
                saveToDatabase(finalTitle, finalMessage, type)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Failed to process notification", e)
            Result.failure()
        }
    }

    // Helper functions for time calculation (Put these below doWork inside ReminderWorker)
    private fun getStartOfDay(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun getEndOfDay(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
    }

    private fun showSystemNotification(title: String, message: String, type: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "finance_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Finance Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders and financial summaries"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val expenseIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_action", "add_expense")
        }
        val expensePendingIntent = PendingIntent.getActivity(
            context, 1, expenseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val actionLogExpense = NotificationCompat.Action(
            android.R.drawable.ic_input_add,
            "Log Expense",
            expensePendingIntent
        )

        val inboxIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_action", "view_inbox")
        }
        val inboxPendingIntent = PendingIntent.getActivity(
            context, 2, inboxIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val actionViewInbox = NotificationCompat.Action(
            android.R.drawable.ic_menu_info_details,
            "View Inbox",
            inboxPendingIntent
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .addAction(actionLogExpense)
            .addAction(actionViewInbox)
            .build()

        // We use a unique ID based on the time so multiple notifications don't overwrite each other
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private suspend fun saveToDatabase(title: String, message: String, type: String) {
        try {
            // Fetch your specific database instance using the application context
            val database = PersonalFinanceCompanionDatabase.getInstance(context.applicationContext)

            val newNotification = AppNotification(
                title = title,
                message = message,
                timestampMillis = System.currentTimeMillis(),
                isRead = false,
                type = type
            )

            // Note: I used notificationDoa() exactly as you spelled it in your Database class!
            database.notificationDoa().insertNotification(newNotification)
            Log.d("ReminderWorker", "Successfully saved to Room Database!")

        } catch (e: Exception) {
            Log.e("ReminderWorker", "Database Save Failed: ${e.message}", e)
        }
    }
}