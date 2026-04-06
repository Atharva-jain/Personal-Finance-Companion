package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.view_model.HomeViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.cards.BalanceCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.cards.EmptyChartStateCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.cards.SmartInsightCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.chart.CategoryBreakdownChart
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.header.HeaderSection
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.section.RecentTransactionsSection
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.section.SavingsGoalSection
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants.USER_NAME


@Composable
fun HomeScreen(
    viewModel: HomeViewModel, navController: NavHostController, modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is Resources.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )

            is Resources.Error -> Text(
                state.message ?: "An error occurred",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )

            is Resources.Success -> {
                val data = state.data ?: return

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // Header
                    HeaderSection(userName = USER_NAME, {
                        navController.navigate(Constants.NOTIFICATION_SCREEN)
                    })

                    // Main Balance Card
                    BalanceCard(data.totalBalance, data.totalIncome, data.totalExpense)

                    // Smart Cash Flow Insight
                    if (data.highestExpenseCategory != "N/A" && data.highestExpensePercentage > 0) {
                        SmartInsightCard(
                            data.highestExpenseCategory,
                            data.highestExpensePercentage,
                            data.topGoalName
                        )
                    }

                    // Savings Goal
                    if (data.topGoalName != "No Active Goals") {
                        SavingsGoalSection(data.topGoalName, data.topGoalSaved, data.topGoalTarget)
                    }

                    // Recent Transactions List
                    if (data.recentTransactions.isNotEmpty()) {
                        RecentTransactionsSection(data.recentTransactions, navController)
                    }

                    // Category Chart
                    if (data.categoryExpenses.isNotEmpty()) {
                        CategoryBreakdownChart(data.categoryExpenses)
                    } else {
                        EmptyChartStateCard()
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            is Resources.Idle -> {}
        }
    }
}






