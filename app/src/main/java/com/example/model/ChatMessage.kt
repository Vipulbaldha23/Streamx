package com.example.model

enum class ChatBadge {
    NONE,
    MODERATOR,
    VIP,
    TOP_FAN,
    STREAMER
}

data class LiveChatMessage(
    val id: String,
    val streamId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val badge: ChatBadge = ChatBadge.NONE,
    val isSystemAnnouncement: Boolean = false,
    val giftNotice: String? = null
)
