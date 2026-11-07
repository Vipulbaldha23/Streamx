package com.example.model

data class VirtualGift(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val coinCost: Int,
    val effectDurationMs: Long = 3000L,
    val animationType: GiftAnimationType = GiftAnimationType.BURST,
    val description: String = ""
)

enum class GiftAnimationType {
    BURST,
    ROCKET_FLY,
    CROWN_SHINE,
    FIREWORKS,
    RAIN
}

data class GiftTransaction(
    val id: String,
    val senderId: String,
    val senderName: String,
    val streamerId: String,
    val gift: VirtualGift,
    val timestamp: Long = System.currentTimeMillis()
)
