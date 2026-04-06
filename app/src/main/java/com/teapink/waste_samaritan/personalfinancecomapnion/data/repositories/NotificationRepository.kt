package com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories

import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.NotificationDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.AppNotification
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class NotificationRepository(private val notificationDao: NotificationDao) {

    // Safely wrap the Room Flow into your Resources state wrapper
    fun getAllNotifications(): Flow<Resources<List<AppNotification>>> {
        return notificationDao.getAllNotifications().map { notifications ->
                Resources.Success(notifications) as Resources<List<AppNotification>>
            }.onStart {
                emit(Resources.Loading())
            }.catch { exception ->
                emit(Resources.Error(exception.message ?: "Failed to load notifications"))
            }
    }

    suspend fun insertNotification(notification: AppNotification) {
        notificationDao.insertNotification(notification)
    }

    suspend fun markAsRead(id: Int) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(notification: AppNotification) {
        notificationDao.deleteNotification(notification)
    }
}