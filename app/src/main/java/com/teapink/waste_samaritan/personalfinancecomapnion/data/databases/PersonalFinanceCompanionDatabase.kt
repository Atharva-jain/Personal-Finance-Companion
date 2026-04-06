package com.teapink.waste_samaritan.personalfinancecomapnion.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.ChallengeDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.GoalDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.NotificationDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.TransactionDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.SavingsGoalEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.convertor.Converters


@Database(
    entities = [Transaction::class, SavingsGoalEntity::class, ChallengeEntity::class, AppNotification::class],
    version = 2,
    exportSchema = false // Set to true to work with the Room Gradle Plugin
)
@TypeConverters(Converters::class)
abstract class PersonalFinanceCompanionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun goalDoa(): GoalDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun notificationDoa(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: PersonalFinanceCompanionDatabase? = null

        fun getInstance(context: Context): PersonalFinanceCompanionDatabase {
            // If INSTANCE is not null, return it.
            // If it is, create the database in a thread-safe way.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersonalFinanceCompanionDatabase::class.java,
                    "finance_database" // Renamed for relevance
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}