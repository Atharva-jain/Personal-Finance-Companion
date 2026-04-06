package com.teapink.waste_samaritan.personalfinancecomapnion.utils.dummy_data

import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.SavingsGoalEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.CAT_BILLS
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.CAT_FOOD
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.CAT_HEALTH
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.CAT_SHOPPING
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.CAT_TRANSPORT
import java.util.Calendar

object DummyDataGenerator {

    fun getDummyGoals(): List<SavingsGoalEntity> {
        return listOf(
            SavingsGoalEntity(
                title = "MacBook Pro",
                targetAmount = 120000.0,
                savedAmount = 45000.0,
                iconName = "Laptop",
                colorArgb = 0xFF2962FF
            ), SavingsGoalEntity(
                title = "Emergency Fund",
                targetAmount = 500000.0,
                savedAmount = 150000.0,
                iconName = "Health",
                colorArgb = 0xFF00C853
            ), SavingsGoalEntity(
                title = "Goa Trip",
                targetAmount = 30000.0,
                savedAmount = 12000.0,
                iconName = "Flight",
                colorArgb = 0xFFFFB300
            )
        )
    }

    fun getDummyChallenges(): List<ChallengeEntity> {
        return listOf(
            ChallengeEntity(
                title = "Coffee Skip",
                currentDays = 12,
                targetDays = 30,
                iconName = "Fire",
                colorArgb = 0xFF9C27B0
            ), ChallengeEntity(
                title = "No Swiggy/Zomato",
                currentDays = 5,
                targetDays = 15,
                iconName = "Fire",
                colorArgb = 0xFFFF5252
            )
        )
    }

    fun getDummyTransactionsFor6Months(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val calendar = Calendar.getInstance()

        // Step back exactly 6 months
        calendar.add(Calendar.MONTH, -6)

        val categories = listOf(CAT_FOOD, CAT_TRANSPORT, CAT_BILLS, CAT_SHOPPING, CAT_HEALTH)

        // Loop through the last 6 months
        for (month in 1..6) {
            // 1. Add Monthly Salary on the 1st of the month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            transactions.add(
                Transaction(
                    amount = 65000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    dateMillis = calendar.timeInMillis,
                    note = "Monthly Tech Salary"
                )
            )

            // 2. Add Fixed Rent on the 5th
            calendar.set(Calendar.DAY_OF_MONTH, 5)
            transactions.add(
                Transaction(
                    amount = 15000.0,
                    type = TransactionType.EXPENSE,
                    category = "Housing",
                    dateMillis = calendar.timeInMillis,
                    note = "Apartment Rent"
                )
            )

            // 3. Generate 10-15 random expenses per month
            val randomExpenseCount = (10..15).random()
            for (i in 0 until randomExpenseCount) {
                // Pick a random day between 2 and 28
                val randomDay = (2..28).random()
                calendar.set(Calendar.DAY_OF_MONTH, randomDay)

                val randomCategory = categories.random()
                val randomAmount = when (randomCategory) {
                    "Dining Out" -> (300..1500).random().toDouble()
                    "Groceries" -> (1000..5000).random().toDouble()
                    "Transport" -> (100..800).random().toDouble()
                    "Entertainment" -> (500..2000).random().toDouble()
                    else -> (1000..3000).random().toDouble()
                }

                transactions.add(
                    Transaction(
                        amount = randomAmount,
                        type = TransactionType.EXPENSE,
                        category = randomCategory,
                        dateMillis = calendar.timeInMillis,
                        note = "UPI Payment"
                    )
                )
            }

            // Move forward to the next month
            calendar.add(Calendar.MONTH, 1)
        }

        return transactions
    }
}