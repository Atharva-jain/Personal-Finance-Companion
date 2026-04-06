package com.teapink.waste_samaritan.personalfinancecomapnion.data.di


import androidx.room.Room
import com.teapink.waste_samaritan.personalfinancecomapnion.data.databases.PersonalFinanceCompanionDatabase
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.ChallengeRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.GoalRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.NotificationRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.TransactionRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.GoalViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel.TransactionViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model.HomeViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.InsightsViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model.NotificationViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.view_model.SettingViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.bio_metric.SecurityPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.CurrencyPreferences

import com.teapink.waste_samaritan.personalfinancecomapnion.utils.notification_and_reminder.NotificationPreferences
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.theme.ThemePreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val modules = module {

    single {
        PersonalFinanceCompanionDatabase.getInstance(androidContext())
    }

    single {
        ThemePreferences(androidContext())
    }
    single {
        NotificationPreferences(androidContext())
    }
    single {
        CurrencyPreferences(androidContext())
    }
    single {
        SecurityPreferences(androidContext())
    }


    // 2. Provide the DAO (Singleton)
    single {
        get<PersonalFinanceCompanionDatabase>().transactionDao()
    }

    single {
        get<PersonalFinanceCompanionDatabase>().goalDoa()
    }

    single {
        get<PersonalFinanceCompanionDatabase>().challengeDao()
    }

    single {
        get<PersonalFinanceCompanionDatabase>().notificationDoa()
    }

    single<TransactionRepository> {
        TransactionRepository(get())
    }

    single<GoalRepository> {
        GoalRepository(get())
    }

    single<ChallengeRepository> {
        ChallengeRepository(get())
    }

    single<NotificationRepository> {
        NotificationRepository(get())
    }

    // 4. Provide the ViewModels
    viewModel<HomeViewModel> { HomeViewModel(get(), get()) }
    viewModel<TransactionViewModel> { TransactionViewModel(get()) }
    viewModel<GoalViewModel> { GoalViewModel(get(), get()) }
    viewModel<SettingViewModel> {
        SettingViewModel(
            get(), get(), get(), get(), get(), get(), get(), androidContext()
        )
    }
    viewModel<InsightsViewModel> { InsightsViewModel(get()) }
    viewModel<NotificationViewModel> { NotificationViewModel(get()) }
}