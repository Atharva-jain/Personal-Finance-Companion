package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.InsightsViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import java.text.NumberFormat
import java.util.Locale
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards.MonthlyTrendCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards.HighlightCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards.WalletHealthCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards.WeekOverWeekCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.charts.CategoryPieChartSection
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.header.InsightHeader
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrencyWithMaxFraction


@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier, viewModel: InsightsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is Resources.Success) startAnimation = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is Resources.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is Resources.Error -> Text(
                state.message ?: "Error", modifier = Modifier.align(Alignment.Center)
            )

            is Resources.Success -> {
                val insights = state.data ?: return@Box

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    // AI Summary Header
                    item { InsightHeader(insights.thisWeekSpend, insights.lastWeekSpend) }

                    // Wallet Health Overview ---
                    item {
                        WalletHealthCard(
                            insights.totalIncome, insights.totalExpense, startAnimation
                        )
                    }

                    // Horizontal Scrollable Highlight Cards (Expanded!)
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            item {
                                WeekOverWeekCard(
                                    insights.thisWeekSpend, insights.lastWeekSpend, startAnimation
                                )
                            }
                            // Biggest Purchase Card
                            item {
                                HighlightCard(
                                    title = "Biggest Purchase",
                                    value = insights.biggestPurchaseName,
                                    subValue = formatCurrencyWithMaxFraction(insights.biggestPurchaseAmount),
                                    icon = Icons.Rounded.ShoppingBag,
                                    bgColor = Color(0xFFE1BEE7), // Soft Purple
                                    iconColor = Color(0xFF6A1B9A)
                                )
                            }
                            // Busiest Day Card
                            item {
                                HighlightCard(
                                    title = "Busiest Day",
                                    value = insights.busiestDay,
                                    subValue = "Most swipes",
                                    icon = Icons.Rounded.EventBusy,
                                    bgColor = Color(0xFFB2EBF2), // Soft Cyan
                                    iconColor = Color(0xFF00838F)
                                )
                            }
                            item {
                                HighlightCard(
                                    "Top Category",
                                    insights.highestCategory,
                                    formatCurrencyWithMaxFraction(insights.highestCategoryAmount),
                                    Icons.Rounded.LocalFireDepartment,
                                    Color(0xFFFFEBEE),
                                    Color(0xFFD32F2F)
                                )
                            }
                            item {
                                HighlightCard(
                                    "Most Frequent",
                                    insights.frequentType,
                                    "${insights.frequentCount} times",
                                    Icons.Rounded.SyncAlt,
                                    Color(0xFFE8EAF6),
                                    Color(0xFF3F51B5)
                                )
                            }
                        }
                    }

                    // 4. Animated Pie Chart for Categories
                    item {
                        if (insights.categoryBreakdown.isNotEmpty()) {
                            CategoryPieChartSection(insights.categoryBreakdown, startAnimation)
                        }
                    }

                    // 5. Monthly Trend Bar Chart
                    item { MonthlyTrendCard(insights.monthlyData, startAnimation) }
                }
            }

            is Resources.Idle -> {}
        }
    }
}



