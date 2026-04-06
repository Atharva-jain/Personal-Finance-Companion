package com.teapink.waste_samaritan.personalfinancecomapnion.ui.navigator.screens

import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants

sealed class Screens(val route: String) {
    object HOME_SCREEN: Screens(Constants.HOME_SCREEN)
    object TRANSACTIONS_SCREEN: Screens(Constants.TRANSACTIONS_SCREEN)
    object INSIGHTS_SCREEN: Screens(Constants.INSIGHTS_SCREEN)
    object GOAL_SCREEN: Screens(Constants.GOAL_SCREEN)
    object SETTING_SCREEN: Screens(Constants.SETTING_SCREEN)
    object NOTIFICATION_SCREEN_SETTING: Screens(Constants.NOTIFICATION_SCREEN_SETTING)
    object NOTIFICATION_SCREEN: Screens(Constants.NOTIFICATION_SCREEN)
}