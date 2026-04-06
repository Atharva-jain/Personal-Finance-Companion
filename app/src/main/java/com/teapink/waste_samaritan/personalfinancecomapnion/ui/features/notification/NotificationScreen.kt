package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification

import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model.NotificationViewModel


import androidx.compose.animation.*
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources

import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.empty.EmptyInboxState
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.swipe.SwipeToDeleteNotificationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is Resources.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is Resources.Error -> {
                Text(
                    text = state.message ?: "Failed to load notifications.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is Resources.Success -> {
                val notifications = state.data ?: emptyList()
                val unreadCount = notifications.count { !it.isRead }

                if (notifications.isEmpty()) {
                    EmptyInboxState()
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Animated Header
                        AnimatedVisibility(
                            visible = unreadCount > 0,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$unreadCount Unread",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    onClick = { viewModel.markAllAsRead() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Mark all as read", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp, start = 4.dp, end = 4.dp)
                        ) {
                            items(items = notifications, key = { it.id }) { notification ->
                                SwipeToDeleteNotificationItem(
                                    notification = notification,
                                    onDelete = { viewModel.deleteNotification(notification) },
                                    onClick = { viewModel.markAsRead(notification.id) },
                                    modifier = Modifier.animateItem() // Silky smooth list reordering
                                )
                            }
                        }
                    }
                }
            }
            is Resources.Idle -> {}
        }
    }
}

// --- VISUAL COMPONENTS ---





