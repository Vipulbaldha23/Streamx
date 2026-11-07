package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminSettings(
    val platformCommissionPercent: Double = 30.0,
    val coinConversionRateInr: Double = 1.0, // 1 Coin = 1 INR
    val minimumPayoutInr: Double = 500.0,
    val isAutoModerationEnabled: Boolean = true
)

class AdminRepository {
    private val _settings = MutableStateFlow(AdminSettings())
    val settings: StateFlow<AdminSettings> = _settings.asStateFlow()

    private val _bannedUsers = MutableStateFlow<Set<String>>(emptySet())
    val bannedUsers: StateFlow<Set<String>> = _bannedUsers.asStateFlow()

    fun updateSettings(commission: Double, coinRate: Double, minPayout: Double) {
        _settings.value = _settings.value.copy(
            platformCommissionPercent = commission,
            coinConversionRateInr = coinRate,
            minimumPayoutInr = minPayout
        )
    }

    fun banUser(userId: String) {
        _bannedUsers.value = _bannedUsers.value + userId
    }

    fun unbanUser(userId: String) {
        _bannedUsers.value = _bannedUsers.value - userId
    }
}
