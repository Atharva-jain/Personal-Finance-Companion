package com.teapink.waste_samaritan.personalfinancecomapnion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.theme.PersonalFinanceComapnionTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.GoalViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel.TransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model.HomeViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.InsightsViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.lock.LockScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.main.MainScreenHolder
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model.NotificationViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.theme.ThemePreferences
import org.koin.android.ext.android.inject


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeViewModel: HomeViewModel by viewModel()
        val transactionViewModel: TransactionViewModel by viewModel()
        val goalViewModel: GoalViewModel by viewModel()
        val insightsViewModel: InsightsViewModel by viewModel()
        val settingViewModel: SettingViewModel by viewModel()
        val notificationViewModel: NotificationViewModel by viewModel()
        val themePreferences: ThemePreferences by inject()
        val notificationPreferences: NotificationPreferences by inject()

        setContent {
            val navController = rememberNavController()
            val isDarkMode by themePreferences.themeFlow.collectAsState()
            val isAppLocked by settingViewModel.isAppLockEnabled.collectAsStateWithLifecycle()
            var hasUnlockedThisSession by rememberSaveable { mutableStateOf(false) }

            PersonalFinanceComapnionTheme(
                darkTheme = isDarkMode
            ) {
                //THE BIOMETRIC INTERCEPTOR
                if (isAppLocked && !hasUnlockedThisSession) {
                    // Show the lock screen and halt loading the rest of the app
                    LockScreen(
                        onUnlocked = {
                            hasUnlockedThisSession = true
                        }
                    )
                } else {
                    // Either the app isn't locked, or they successfully scanned their fingerprint.
                    // Load the normal app!
                    MainScreenHolder(
                        homeViewModel = homeViewModel,
                        transactionViewModel = transactionViewModel,
                        navController = navController,
                        goalViewModel = goalViewModel,
                        insightsViewModel = insightsViewModel,
                        settingViewModel = settingViewModel,
                        notificationViewModel = notificationViewModel,
                        themePreferences = themePreferences,
                        notificationPreferences = notificationPreferences
                    )
                }
            }
        }
    }
}

