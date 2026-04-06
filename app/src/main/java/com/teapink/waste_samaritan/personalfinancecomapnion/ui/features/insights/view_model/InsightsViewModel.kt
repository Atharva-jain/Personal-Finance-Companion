package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.TransactionRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- UPDATED UI Data Model ---
data class DashboardInsights(
    val thisWeekSpend: Double,
    val lastWeekSpend: Double,
    val highestCategory: String,
    val highestCategoryAmount: Double,
    val frequentType: String,
    val frequentCount: Int,
    val monthlyData: List<Pair<String, Float>>,
    val categoryBreakdown: List<CategorySpend>,
    val totalIncome: Double,
    val totalExpense: Double,
    val biggestPurchaseName: String,
    val biggestPurchaseAmount: Double,
    val busiestDay: String
)

data class CategorySpend(
    val name: String,
    val amount: Double,
    val percent: Float,
    val color: Color
)

// --- ViewModel ---
class InsightsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<Resources<DashboardInsights>> = transactionRepository.getAllTransactions()
        .map { resource ->
            when (resource) {
                is Resources.Loading -> Resources.Loading()
                is Resources.Error -> Resources.Error(resource.message ?: "Unknown Error")
                is Resources.Idle -> Resources.Idle()
                is Resources.Success -> {
                    val transactions = resource.data ?: emptyList()
                    Resources.Success(calculateInsights(transactions))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resources.Loading())

    private fun calculateInsights(transactions: List<Transaction>): DashboardInsights {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val income = transactions.filter { it.type == TransactionType.INCOME }

        // 1. New Cash Flow & Single Purchase Logic
        val totalIncome = income.sumOf { it.amount }
        val totalExpense = expenses.sumOf { it.amount }
        val biggestExpense = expenses.maxByOrNull { it.amount }

        // 2. Time Calculations for Week-over-Week
        val now = System.currentTimeMillis()
        val oneWeek = 7 * 24 * 60 * 60 * 1000L
        val oneWeekAgo = now - oneWeek
        val twoWeeksAgo = now - (2 * oneWeek)

        val thisWeekSpend = expenses.filter { it.dateMillis >= oneWeekAgo }.sumOf { it.amount }
        val lastWeekSpend = expenses.filter { it.dateMillis in twoWeeksAgo until oneWeekAgo }.sumOf { it.amount }

        // 3. Category & Frequency Analysis
        val categoryMap = expenses.groupBy { it.category }.mapValues { e -> e.value.sumOf { it.amount } }
        val highestCat = categoryMap.maxByOrNull { it.value }
        val typeMap = expenses.groupBy { it.category }.mapValues { e -> e.value.size }
        val frequentType = typeMap.maxByOrNull { it.value }

        // 4. NEW: Busiest Day of the Week Logic
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Returns "Monday", "Tuesday", etc.
        val dayMap = expenses.groupBy { dayFormat.format(Date(it.dateMillis)) }.mapValues { e -> e.value.size }
        val busiestDay = dayMap.maxByOrNull { it.value }?.key ?: "N/A"

        // 5. Monthly Trend Logic (Already fixed previously!)
        val monthlyTrendData = mutableListOf<Pair<String, Float>>()
        val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthlyTotals = mutableListOf<Double>()
        val monthNames = mutableListOf<String>()

        for (i in 5 downTo 0) {
            val tempCal = Calendar.getInstance()
            tempCal.add(Calendar.MONTH, -i)
            val monthIndex = tempCal.get(Calendar.MONTH)
            val year = tempCal.get(Calendar.YEAR)

            val totalForMonth = expenses.filter {
                val tCal = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
                tCal.get(Calendar.MONTH) == monthIndex && tCal.get(Calendar.YEAR) == year
            }.sumOf { it.amount }

            monthlyTotals.add(totalForMonth)
            monthNames.add(monthLabels[monthIndex])
        }

        val maxSpend = monthlyTotals.maxOrNull()?.takeIf { it > 0 } ?: 1.0
        monthNames.forEachIndexed { index, name ->
            val percentage = ((monthlyTotals[index] / maxSpend) * 100).toFloat()
            val finalValue = if (monthlyTotals[index] > 0 && percentage < 5f) 5f else percentage
            monthlyTrendData.add(name to finalValue)
        }

        // 6. Category Breakdown for Pie Chart
        val palette = listOf(Color(0xFF6200EA), Color(0xFF00BFA5), Color(0xFFFFAB00), Color(0xFFD50000), Color(0xFF2962FF))
        val categoryBreakdown = categoryMap.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapIndexed { index, entry ->
                CategorySpend(
                    name = entry.key,
                    amount = entry.value,
                    percent = if (totalExpense > 0) (entry.value / totalExpense).toFloat() else 0f,
                    color = palette[index % palette.size]
                )
            }

        return DashboardInsights(
            thisWeekSpend = thisWeekSpend,
            lastWeekSpend = lastWeekSpend,
            highestCategory = highestCat?.key ?: "N/A",
            highestCategoryAmount = highestCat?.value ?: 0.0,
            frequentType = frequentType?.key ?: "N/A",
            frequentCount = frequentType?.value ?: 0,
            monthlyData = monthlyTrendData,
            categoryBreakdown = categoryBreakdown,
            // Insert new metrics into the model
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            biggestPurchaseName = biggestExpense?.category ?: "N/A",
            biggestPurchaseAmount = biggestExpense?.amount ?: 0.0,
            busiestDay = busiestDay
        )
    }
}