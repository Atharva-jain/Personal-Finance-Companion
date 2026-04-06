package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.header

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrencyWithMaxFraction


@Composable
fun InsightHeader(thisWeek: Double, lastWeek: Double) {
    val difference = lastWeek - thisWeek
    Column {
        Text("Your Financial Pulse", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Text(
            text = if (difference >= 0) "You're saving ${formatCurrencyWithMaxFraction(difference)} compared to last week! 🚀"
            else "Spending is up ${formatCurrencyWithMaxFraction(Math.abs(difference))} this week. 📉",
            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground
        )
    }
}