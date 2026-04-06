package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.charts

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.view_model.CategorySpend
import kotlin.collections.forEach

@Composable
fun CategoryPieChartSection(categories: List<CategorySpend>, startAnimation: Boolean) {
    val animatedProgress by animateFloatAsState(targetValue = if (startAnimation) 360f else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Pie Chart
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    categories.forEach { category ->
                        val sweepAngle = category.percent * animatedProgress
                        drawArc(color = category.color, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = false, style = Stroke(width = 40f, cap = StrokeCap.Round), size = Size(size.width, size.height))
                        startAngle += (category.percent * 360f)
                    }
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            // Legend
            Column {
                categories.take(4).forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(category.color))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(category.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("${(category.percent * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}
