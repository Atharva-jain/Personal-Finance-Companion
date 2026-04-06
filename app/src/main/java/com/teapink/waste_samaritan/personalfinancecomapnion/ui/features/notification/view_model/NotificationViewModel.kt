package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.notification.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.NotificationRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    // Now uses the Resources wrapper for robust UI state handling
    val uiState: StateFlow<Resources<List<AppNotification>>> =
        repository.getAllNotifications().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resources.Loading()
            )

    fun markAsRead(id: Int) {
        viewModelScope.launch { repository.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { repository.markAllAsRead() }
    }

    fun deleteNotification(notification: AppNotification) {
        viewModelScope.launch { repository.deleteNotification(notification) }
    }
}