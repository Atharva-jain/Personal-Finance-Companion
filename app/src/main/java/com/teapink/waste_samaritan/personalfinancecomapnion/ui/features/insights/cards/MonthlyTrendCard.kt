package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MonthlyTrendCard(data: List<Pair<String, Float>>, startAnimation: Boolean) {
    val animatedProgress by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")

    Card(modifier = Modifier.fillMaxWidth().height(240.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("6-Month Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

            Canvas(modifier = Modifier.fillMaxSize().weight(1f)) {
                val barWidth = 32f
                val spacing = (size.width - (barWidth * data.size)) / (data.size - 1)

                data.forEachIndexed { index, pair ->
                    val x = index * (barWidth + spacing)
                    val targetHeight = (pair.second / 100f) * size.height
                    val animatedHeight = targetHeight * animatedProgress
                    val y = size.height - animatedHeight

                    drawRoundRect(color = surfaceVariant.copy(alpha = 0.3f), topLeft = Offset(x, 0f), size = Size(barWidth, size.height), cornerRadius = CornerRadius(50f, 50f))
                    drawRoundRect(color = if (index == data.size - 1) primaryColor else primaryColor.copy(alpha = 0.5f), topLeft = Offset(x, y), size = Size(barWidth, animatedHeight), cornerRadius = CornerRadius(50f, 50f))
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                data.forEach { Text(it.first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
            }
        }
    }
}
