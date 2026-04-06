package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.insights.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HighlightCard(title: String, value: String, subValue: String, icon: ImageVector, bgColor: Color, iconColor: Color) {
    Card(modifier = Modifier.width(160.dp).height(140.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = iconColor.copy(alpha = 0.8f))
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = iconColor)
                Text(subValue, style = MaterialTheme.typography.labelSmall, color = iconColor.copy(alpha = 0.8f))
            }
        }
    }
}
