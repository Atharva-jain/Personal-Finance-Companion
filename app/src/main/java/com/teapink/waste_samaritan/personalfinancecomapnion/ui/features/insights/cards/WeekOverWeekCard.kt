package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrencyWithMaxFraction

@Composable
fun WeekOverWeekCard(thisWeek: Double, lastWeek: Double, startAnimation: Boolean) {
    val diff = thisWeek - lastWeek
    val percent = if (lastWeek > 0) (Math.abs(diff) / lastWeek) * 100 else 0.0
    val isUp = diff > 0
    val animatedPercent by animateFloatAsState(targetValue = if (startAnimation) percent.toFloat() else 0f, animationSpec = tween(1200), label = "")

    Card(modifier = Modifier.width(200.dp).height(140.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text("This Week", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(formatCurrencyWithMaxFraction(thisWeek), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isUp) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown, contentDescription = null, tint = if (isUp) Color(0xFFD32F2F) else Color(0xFF388E3C), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${animatedPercent.toInt()}% vs last week", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isUp) Color(0xFFD32F2F) else Color(0xFF388E3C))
            }
        }
    }
}
