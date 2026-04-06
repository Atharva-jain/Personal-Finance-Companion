package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.GoalViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel.TransactionViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model.HomeViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.InsightsViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.main.header.DrawerHeader
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.main.items.SmoothDrawerItem
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model.NotificationViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.navigator.navHost.MainNavHost
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.theme.ThemePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenHolder(
    homeViewModel: HomeViewModel,
    transactionViewModel: TransactionViewModel,
    navController: NavHostController,
    goalViewModel: GoalViewModel,
    insightsViewModel: InsightsViewModel,
    settingViewModel: SettingViewModel,
    notificationViewModel: NotificationViewModel,
    themePreferences: ThemePreferences,
    notificationPreferences: NotificationPreferences
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 1. THE SINGLE SOURCE OF TRUTH: Listen directly to the NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Constants.HOME_SCREEN

    // 2. Derive the Top App Bar Title dynamically from the current route
    val topBarTitle = when (currentRoute) {
        Constants.HOME_SCREEN -> "Dashboard"
        Constants.TRANSACTIONS_SCREEN -> Constants.TRANSACTIONS_SCREEN
        Constants.INSIGHTS_SCREEN -> Constants.INSIGHTS_SCREEN
        Constants.GOAL_SCREEN -> Constants.GOAL_SCREEN
        Constants.SETTING_SCREEN -> Constants.SETTING_SCREEN
        Constants.NOTIFICATION_SCREEN -> Constants.NOTIFICATION_SCREEN
        Constants.NOTIFICATION_SCREEN_SETTING -> Constants.NOTIFICATION_SCREEN_SETTING
        else -> "Finance App"
    }

    val topLevelRoutes = listOf(
        Constants.HOME_SCREEN,
        Constants.TRANSACTIONS_SCREEN,
        Constants.INSIGHTS_SCREEN,
        Constants.SETTING_SCREEN
    )
    // Check if we are on a main screen where the Drawer should be accessible
    val isTopLevelScreen = topLevelRoutes.contains(currentRoute)

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        gesturesEnabled = isTopLevelScreen,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(320.dp)
            ) {
                // Header
                DrawerHeader()

                Spacer(modifier = Modifier.height(16.dp))

                // Drawer Items
                SmoothDrawerItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentRoute == Constants.HOME_SCREEN
                ) {
                    scope.launch { drawerState.close() }
                    navigateToRoute(navController, Constants.HOME_SCREEN)
                }

                SmoothDrawerItem(
                    label = "Transactions",
                    icon = Icons.Default.List,
                    isSelected = currentRoute == Constants.TRANSACTIONS_SCREEN
                ) {
                    scope.launch { drawerState.close() }
                    navigateToRoute(navController, Constants.TRANSACTIONS_SCREEN)
                }

                SmoothDrawerItem(
                    label = "Insights",
                    icon = Icons.Default.Analytics,
                    isSelected = currentRoute == Constants.INSIGHTS_SCREEN
                ) {
                    scope.launch { drawerState.close() }
                    navigateToRoute(navController, Constants.INSIGHTS_SCREEN)
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes settings to the bottom

                SmoothDrawerItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = currentRoute == Constants.SETTING_SCREEN
                ) {
                    scope.launch { drawerState.close() }
                    navigateToRoute(navController, Constants.SETTING_SCREEN)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }) {
        // Main Screen Content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (isTopLevelScreen) {
                            // Show Hamburger Menu on main screens
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        } else {
                            // Show Back Arrow on sub-screens (like Goals)
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    // AutoMirrored ensures it flips correctly for Right-to-Left languages
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Navigate Back"
                                )
                            }
                        }
                    },
                    // --- ADD THIS ACTIONS BLOCK ---
                    actions = {
                        // Only show the Goals shortcut when we are on the Home Screen
                        if (currentRoute == Constants.HOME_SCREEN) {
                            IconButton(
                                onClick = {
                                    // Navigate to the Insights/Goals screen
                                    navController.navigate(Constants.GOAL_SCREEN) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                // Use a 'Trophy' or 'TrackChanges' icon for Goals
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = "Goals & Challenges",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    })
            }) { innerPadding ->
            MainNavHost(
                viewModel = homeViewModel,
                transactionViewModel = transactionViewModel,
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                paddingValues = innerPadding,
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


// 5. HELPER FUNCTION: Prevents the back button from closing the app or building massive back stacks
fun navigateToRoute(navController: NavHostController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to avoid building up a large stack
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}