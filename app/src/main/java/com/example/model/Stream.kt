package com.example.model

enum class StreamCategory(val displayName: String, val icon: String) {
    ALL("All", "⚡"),
    GAMING("Gaming", "🎮"),
    TECH("Tech & AI", "💻"),
    MUSIC("Live Music", "🎵"),
    CHATTING("IRL / Chit-Chat", "💬"),
    FITNESS("Fitness & Yoga", "🧘"),
    ESPORTS("Esports", "🏆"),
    CREATIVE("Art & Design", "🎨")
}

enum class StreamQuality(val label: String, val resolution: String, val bitrate: String) {
    AUTO("Auto (1080p)", "1080p 60fps", "6000 kbps"),
    FHD("1080p (Full HD)", "1920x1080 60fps", "6000 kbps"),
    HD("720p (High Def)", "1280x720 30fps", "3500 kbps"),
    SD("480p (Data Saver)", "854x480 30fps", "1500 kbps")
}

data class LiveStream(
    val id: String,
    val streamerId: String,
    val streamerName: String,
    val streamerHandle: String,
    val streamerAvatar: String,
    val title: String,
    val category: StreamCategory,
    val viewerCount: Int,
    val likesCount: Int,
    val startedAt: Long,
    val thumbnailUrl: String,
    val quality: StreamQuality = StreamQuality.AUTO,
    val isLive: Boolean = true,
    val isFollowed: Boolean = false,
    val streamTags: List<String> = emptyList(),
    val language: String = "English",
    val bitRateKbps: Int = 4500,
    val fps: Int = 60,
    val pingMs: Int = 24
)

data class StreamReport(
    val id: String,
    val streamId: String,
    val reportedUserId: String,
    val reason: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
