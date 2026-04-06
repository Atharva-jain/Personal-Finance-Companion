package com.teapink.waste_samaritan.personalfinancecomapnion

import android.app.Application
import com.teapink.waste_samaritan.personalfinancecomapnion.data.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PersonalFinanceCompanionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            // Pass the Android context
            androidContext(this@PersonalFinanceCompanionApplication)
            // Load your modules
            modules(modules)
        }
    }
}

