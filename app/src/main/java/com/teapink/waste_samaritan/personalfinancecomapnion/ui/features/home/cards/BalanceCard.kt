package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrencyWithMaxFraction
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.items.TransactionSummaryItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.text.AutoResizingText


@Composable
fun BalanceCard(balance: Double, income: Double, expense: Double) {

    var startAnimation by remember { mutableStateOf(false) }

    val animatedBalance by animateFloatAsState(
        targetValue = if (startAnimation) balance.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "balanceAnim"
    )
    val animatedIncome by animateFloatAsState(
        targetValue = if (startAnimation) income.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "incomeAnim"
    )
    val animatedExpense by animateFloatAsState(
        targetValue = if (startAnimation) expense.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "expenseAnim"
    )

    LaunchedEffect(Unit) { startAnimation = true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Uses the new Auto-resizing text!
            AutoResizingText(
                text = formatCurrencyWithMaxFraction(animatedBalance.toDouble()),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // The exact 50/50 split container
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TransactionSummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Income",
                    amount = animatedIncome.toDouble(),
                    icon = Icons.Rounded.ArrowDownward,
                    color = Color(0xFF4CAF50) // Green
                )
                TransactionSummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Expenses",
                    amount = animatedExpense.toDouble(),
                    icon = Icons.Rounded.ArrowUpward,
                    color = Color(0xFFFF5252) // Red
                )
            }
        }
    }
}


