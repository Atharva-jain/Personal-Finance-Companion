package com.teapink.waste_samaritan.personalfinancecomapnion.ui.navigator.navHost

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel.TransactionViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.GoalsAndChallengesScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.GoalViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.HomeScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model.HomeViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.InsightsScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.InsightsViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.NotificationScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model.NotificationViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.notification.NotificationPreferencesScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.SettingsScreen
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionScreen

import com.teapink.waste_samaritan.personalfinancecomapnion.ui.navigator.screens.Screens
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.theme.ThemePreferences

@Composable
fun MainNavHost(
    viewModel: HomeViewModel,
    transactionViewModel: TransactionViewModel,
    goalViewModel: GoalViewModel,
    navController: NavHostController,
    modifier: Modifier,
    paddingValues: PaddingValues,
    settingViewModel: SettingViewModel,
    insightsViewModel: InsightsViewModel,
    themePreferences: ThemePreferences,
    notificationPreferences: NotificationPreferences,
    notificationViewModel: NotificationViewModel,
) {

    NavHost(
        navController = navController,
        startDestination = Screens.HOME_SCREEN.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400) // 400ms duration for a luxurious feel
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }

    ) {
        composable(Screens.HOME_SCREEN.route) {
            HomeScreen(
                viewModel, navController, modifier
            )
        }
        composable(Screens.TRANSACTIONS_SCREEN.route) {
            TransactionScreen(transactionViewModel, modifier)
        }
        composable(Screens.INSIGHTS_SCREEN.route) {
            InsightsScreen(
                viewModel = insightsViewModel, modifier = modifier
            )
        }
        composable(Screens.GOAL_SCREEN.route) {
            GoalsAndChallengesScreen(modifier, goalViewModel)
        }
        composable(Screens.SETTING_SCREEN.route) {
            SettingsScreen(modifier, settingViewModel, navController)
        }
        composable(Screens.NOTIFICATION_SCREEN_SETTING.route) {
            NotificationPreferencesScreen(
                navController = navController, viewModel = settingViewModel, modifier = modifier
            )
        }
        composable(Screens.NOTIFICATION_SCREEN.route) {
            NotificationScreen(
                navController = navController,
                viewModel = notificationViewModel,
                modifier = modifier
            )
        }
    }

}