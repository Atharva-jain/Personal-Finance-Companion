package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.settings.row

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color,
    showForwardArrow: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    // Smooth background color change on press/click
    var isPressed by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
        animationSpec = tween(200),
        label = "bg_anim"
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(bgColor)
        .clickable {
            isPressed = true
            onClick()
            isPressed = false
        }
        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = iconTint) }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else if (showForwardArrow) {
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = "Forward",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}