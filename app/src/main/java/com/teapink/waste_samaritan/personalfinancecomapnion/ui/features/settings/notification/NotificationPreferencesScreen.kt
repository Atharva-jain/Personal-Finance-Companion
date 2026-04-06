package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.groups.SettingsGroup
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.row.SettingsSwitchRow
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationHelper
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    navController: NavHostController,
    viewModel: SettingViewModel,
    modifier: Modifier = Modifier
) {
    val masterEnabled by viewModel.masterNotifications.collectAsStateWithLifecycle()
    val dailyEnabled by viewModel.dailyReminders.collectAsStateWithLifecycle()
    val weeklyEnabled by viewModel.weeklySummaries.collectAsStateWithLifecycle()
    val goalEnabled by viewModel.goalAlerts.collectAsStateWithLifecycle()
    val challengesEnabled by viewModel.challenges.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleMasterNotifications(context, true)
            scope.launch { snackbarHostState.showSnackbar("Notifications Enabled!") }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permission denied. Cannot send alerts.") }
            viewModel.toggleMasterNotifications(context, false)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { internalPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                // 1. ONLY apply the bottom padding here so we don't overlap navigation
                .padding(bottom = internalPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(24.dp),
            // 2. Set top to 0.dp so it stays perfectly flush with your Top App Bar
            contentPadding = PaddingValues(top = 0.dp, bottom = 32.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. MASTER TOGGLE (WITH PERMISSION LOGIC)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (masterEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    SettingsSwitchRow(
                        title = "Allow Notifications",
                        subtitle = "Enable or disable all app alerts",
                        icon = Icons.Rounded.NotificationsActive,
                        iconTint = if (masterEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        isChecked = masterEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                // If turning ON, check for Android 13+ permission
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) {
                                        viewModel.toggleMasterNotifications(context, true)
                                    } else {
                                        // Ask the OS to show the Permission Dialog
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    // Older Android versions don't need runtime permission for this
                                    viewModel.toggleMasterNotifications(context, true)
                                }
                            } else {
                                // Turning OFF requires no permission
                                viewModel.toggleMasterNotifications(context, false)
                            }
                        })
                }
            }

            // 2. SUB-CATEGORIES
            item {
                AnimatedVisibility(
                    visible = masterEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SettingsGroup(title = "Notification Types") {
                        SettingsSwitchRow(
                            title = "Daily Reminders",
                            subtitle = "Reminds you to log daily expenses",
                            icon = Icons.Rounded.Today,
                            iconTint = Color(0xFF1E88E5),
                            isChecked = dailyEnabled,
                            onCheckedChange = {
                                viewModel.toggleSpecificNotification(
                                    context,
                                    NotificationPreferences.KEY_DAILY_REMINDER,
                                    it
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        SettingsSwitchRow(
                            title = "Weekly Summary",
                            subtitle = "Your spending habits every Sunday",
                            icon = Icons.Rounded.Insights,
                            iconTint = Color(0xFF8E24AA),
                            isChecked = weeklyEnabled,
                            onCheckedChange = {
                                viewModel.toggleSpecificNotification(
                                    context,
                                    NotificationPreferences.KEY_WEEKLY_SUMMARY,
                                    it
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // ADDED: Goal Milestones Toggle
                        SettingsSwitchRow(
                            title = "Goal Milestones",
                            subtitle = "Alerts when you reach savings targets",
                            icon = Icons.Rounded.Flag,
                            iconTint = Color(0xFF4CAF50), // Green
                            isChecked = goalEnabled,
                            onCheckedChange = {
                                viewModel.toggleSpecificNotification(
                                    context,
                                    NotificationPreferences.KEY_GOAL_ALERTS,
                                    it
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        SettingsSwitchRow(
                            title = "Challenge Updates",
                            subtitle = "Alerts for your active financial challenges",
                            icon = Icons.Rounded.Star, // A nice star icon for challenges
                            iconTint = Color(0xFFFFB300), // A vibrant golden yellow
                            isChecked = challengesEnabled,
                            onCheckedChange = { viewModel.toggleSpecificNotification(context, NotificationPreferences.KEY_CHALLENGES, it) }
                        )

                    }
                }
            }

            // 3. TEST BUTTON (To instantly verify it works)
            item {
                AnimatedVisibility(visible = masterEnabled) {
                    Button(
                        // FIXED: Now calls our new function that fires all 4 types!
                        onClick = { NotificationHelper.sendAllTestNotificationsNow(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Send All Test Notifications")
                    }
                }
            }
        }
    }
}