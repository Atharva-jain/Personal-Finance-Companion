package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryBreakdownChart(categoryExpenses: Map<String, Double>) {
    val totalExpense = categoryExpenses.values.sum()
    val categories = categoryExpenses.map { (category, amount) ->
        Pair(category, ((amount / totalExpense) * 100).toFloat())
    }.sortedByDescending { it.second }

    val defaultColors = listOf(Color(0xFF6200EA), Color(0xFF03DAC5), Color(0xFFFFB300), Color(0xFFFF5252), Color(0xFF2962FF), Color(0xFF00C853))

    var animationPlayed by remember { mutableStateOf(false) }
    val sweepProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "chartAnimation"
    )

    LaunchedEffect(categoryExpenses) { animationPlayed = true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Spending Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        var startAngle = -90f
                        categories.forEachIndexed { index, category ->
                            val colorIndex = index % defaultColors.size
                            val sweepAngle = (category.second / 100f) * 360f * sweepProgress
                            drawArc(
                                color = defaultColors[colorIndex], startAngle = startAngle, sweepAngle = sweepAngle,
                                useCenter = false, style = Stroke(width = 35f, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.take(5).forEachIndexed { index, category ->
                        val colorIndex = index % defaultColors.size
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(defaultColors[colorIndex]))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${category.first} (${category.second.toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
