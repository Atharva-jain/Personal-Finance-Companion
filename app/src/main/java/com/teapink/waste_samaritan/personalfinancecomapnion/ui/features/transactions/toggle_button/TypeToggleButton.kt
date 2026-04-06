package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.toggle_button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TypeToggleButton(
    text: String, isSelected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit
) {
    val containerColor =
        if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
        Text(text = text, fontWeight = FontWeight.Bold, color = contentColor)
    }
}