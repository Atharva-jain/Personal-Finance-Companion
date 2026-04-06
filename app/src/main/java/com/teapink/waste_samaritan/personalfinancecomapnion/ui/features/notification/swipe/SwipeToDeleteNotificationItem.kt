package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.swipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.item.NotificationItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteNotificationItem(
    notification: AppNotification,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDeleted by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                isDeleted = true
                onDelete()
                return@rememberSwipeToDismissBoxState true
            }
            false
        }
    )

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(tween(300)),
        modifier = modifier
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

                val color by animateColorAsState(
                    targetValue = if (isSwiping) MaterialTheme.colorScheme.error else Color.Transparent,
                    animationSpec = tween(200),
                    label = "deleteBg"
                )

                val scale by animateFloatAsState(
                    targetValue = if (isSwiping) 1.2f else 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "deleteScale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color)
                        .padding(end = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.scale(scale)
                    )
                }
            },
            enableDismissFromStartToEnd = false
        ) {
            NotificationItemCard(notification, onClick)
        }
    }
}