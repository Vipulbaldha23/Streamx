package com.example.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String = "Content creator & live streamer on StreamX ⚡",
    val followersCount: Int = 1240,
    val followingCount: Int = 186,
    val isVerified: Boolean = true,
    val isLiveNow: Boolean = false,
    val email: String = "",
    val totalStreams: Int = 42,
    val totalDiamondsEarned: Long = 85200L
)

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
