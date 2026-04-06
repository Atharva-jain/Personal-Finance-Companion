package com.teapink.waste_samaritan.personalfinancecomapnion.utils.convertor

import androidx.room.TypeConverter
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType

//@Database(
//    entities = [Transaction::class, SavingsGoalEntity::class, ChallengeEntity::class, AppNotification::class],
//    version = 2,
//    exportSchema = false
//)
//@TypeConverters(Converters::class)
//abstract class FinanceDatabase : RoomDatabase() {
//    abstract fun transactionDao(): TransactionDao
//    abstract fun goalDao(): GoalDao
//    abstract fun challengeDao(): ChallengeDao
//    abstract fun notificationDoa(): NotificationDao
//}

// Type converter for the Enum
class Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(name: String): TransactionType {
        return TransactionType.valueOf(name)
    }
}