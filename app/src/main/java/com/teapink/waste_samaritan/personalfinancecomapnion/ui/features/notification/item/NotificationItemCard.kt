package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.InsertChart
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatRelativeTime


@Composable
fun NotificationItemCard(notification: AppNotification, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "readStateBg"
    )

    val titleWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.ExtraBold

    // FIXED: Added CHALLENGE and SUMMARY mappings so the right icons show up!
    val icon = when (notification.type) {
        "REMINDER" -> Icons.Rounded.NotificationsActive
        "CHALLENGE" -> Icons.Rounded.Star
        "GOAL" -> Icons.Rounded.EmojiEvents
        "SUMMARY" -> Icons.Rounded.InsertChart
        "WARNING" -> Icons.Rounded.WarningAmber
        else -> Icons.Rounded.Info
    }

    // FIXED: Added Golden Star color for Challenges
    val iconTint = when (notification.type) {
        "CHALLENGE" -> Color(0xFFFFB300)
        "WARNING" -> Color(0xFFFF5252)
        "GOAL" -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        // elevation = CardDefaults.cardElevation(if (notification.isRead) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = titleWeight,
                        maxLines = 1, // FIXED: Truncates if too long
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f) // FIXED: Pushes timestamp cleanly
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Safe gap
                    Text(
                        text = formatRelativeTime(notification.timestampMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1 // FIXED: Prevents vertical stacking
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            AnimatedVisibility(
                visible = !notification.isRead,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut(tween(300)) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}