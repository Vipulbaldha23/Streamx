package com.example.data

import com.example.model.AuthState
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val currentUser = User(
        id = "user_streamx_01",
        username = "alex_streamer",
        displayName = "Alex Vance",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
        bio = "Streaming high octane games & interactive creative sessions daily! 🚀",
        followersCount = 4280,
        followingCount = 142,
        email = "alex.streamer@example.com",
        totalStreams = 38,
        totalDiamondsEarned = 142500L
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(currentUser))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, pass: String): Boolean {
        if (email.isNotBlank() && pass.length >= 6) {
            val user = currentUser.copy(email = email)
            _authState.value = AuthState.Authenticated(user)
            return true
        }
        return false
    }

    fun register(name: String, email: String, pass: String): Boolean {
        if (name.isNotBlank() && email.isNotBlank() && pass.length >= 6) {
            val newUser = currentUser.copy(
                displayName = name,
                username = name.lowercase().replace(" ", "_"),
                email = email
            )
            _authState.value = AuthState.Authenticated(newUser)
            return true
        }
        return false
    }

    fun googleSignIn(): Boolean {
        _authState.value = AuthState.Authenticated(currentUser)
        return true
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    fun updateProfile(name: String, bio: String) {
        val current = (_authState.value as? AuthState.Authenticated)?.user ?: return
        _authState.value = AuthState.Authenticated(current.copy(displayName = name, bio = bio))
    }
}
