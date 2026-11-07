package com.example.model

enum class NotificationType {
    LIVE_STARTED,
    NEW_FOLLOWER,
    GIFT_RECEIVED,
    PAYMENT_SUCCESS,
    SYSTEM_ALERT
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetStreamId: String? = null
)
