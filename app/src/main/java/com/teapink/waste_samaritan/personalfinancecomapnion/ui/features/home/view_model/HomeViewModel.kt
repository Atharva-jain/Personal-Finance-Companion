package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model

import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.GoalRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.TransactionRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// 1. UPDATED Data Model
data class DashboardData(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val categoryExpenses: Map<String, Double>,
    val topGoalName: String,
    val topGoalSaved: Double,
    val topGoalTarget: Double,
    val recentTransactions: List<Transaction>,
    val highestExpenseCategory: String,
    val highestExpensePercentage: Int

)

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val uiState: StateFlow<Resources<DashboardData>> = combine(
        transactionRepository.getAllTransactions(), goalRepository.getAllGoals()
    ) { transactionsResource, goalsResource ->

        if (transactionsResource is Resources.Loading || goalsResource is Resources.Loading) {
            return@combine Resources.Loading()
        }

        val transactions = (transactionsResource as? Resources.Success)?.data ?: emptyList()
        val goals = (goalsResource as? Resources.Success)?.data ?: emptyList()

        // Core Financials
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = income - expense

        // Analytics
        val categoryExpenses =
            transactions.filter { it.type == TransactionType.EXPENSE }.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

        val highestCategory = categoryExpenses.maxByOrNull { it.value }
        val highestExpensePercentage = if (expense > 0 && highestCategory != null) {
            ((highestCategory.value / expense) * 100).toInt()
        } else 0

        // Recent Activity
        val recentTransactions = transactions.sortedByDescending { it.dateMillis }.take(3)

        // Goals
        val activeGoals = goals.filter { it.savedAmount < it.targetAmount }
        val featuredGoal =
            activeGoals.maxByOrNull { it.savedAmount / it.targetAmount } ?: goals.firstOrNull()

        Resources.Success(
            DashboardData(
                totalBalance = balance,
                totalIncome = income,
                totalExpense = expense,
                categoryExpenses = categoryExpenses,
                topGoalName = featuredGoal?.title ?: "No Active Goals",
                topGoalSaved = featuredGoal?.savedAmount ?: 0.0,
                topGoalTarget = featuredGoal?.targetAmount?.takeIf { it > 0 } ?: 1.0,
                recentTransactions = recentTransactions,
                highestExpenseCategory = highestCategory?.key ?: "N/A",
                highestExpensePercentage = highestExpensePercentage))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resources.Loading()
    )
}