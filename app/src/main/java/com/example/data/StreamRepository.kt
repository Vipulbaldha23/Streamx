package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class StreamRepository {

    private val initialStreams = listOf(
        LiveStream(
            id = "stream_01",
            streamerId = "creator_raj",
            streamerName = "Aarav Sharma",
            streamerHandle = "@aarav_plays",
            streamerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
            title = "🔥 Grand Championship Finals - Valorant Ranked Gameplay!",
            category = StreamCategory.GAMING,
            viewerCount = 3840,
            likesCount = 14200,
            startedAt = System.currentTimeMillis() - (45 * 60 * 1000L),
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&q=80",
            isLive = true,
            isFollowed = true,
            streamTags = listOf("FPS", "Pro", "Ranked", "Hindi/English"),
            language = "Hindi",
            bitRateKbps = 5800,
            fps = 60
        ),
        LiveStream(
            id = "stream_02",
            streamerId = "creator_priya",
            streamerName = "Priya Kapoor",
            streamerHandle = "@priya_vibes",
            streamerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
            title = "Acoustic Sunset Sessions 🎸 Request your favorite songs!",
            category = StreamCategory.MUSIC,
            viewerCount = 1920,
            likesCount = 8900,
            startedAt = System.currentTimeMillis() - (20 * 60 * 1000L),
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80",
            isLive = true,
            isFollowed = false,
            streamTags = listOf("Acoustic", "LiveSinger", "Chilled"),
            language = "English",
            bitRateKbps = 4500,
            fps = 30
        ),
        LiveStream(
            id = "stream_03",
            streamerId = "creator_neha",
            streamerName = "Neha Verma",
            streamerHandle = "@neha_tech",
            streamerAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&q=80",
            title = "Building Next-Gen Android Apps with Jetpack Compose & AI 💻",
            category = StreamCategory.TECH,
            viewerCount = 850,
            likesCount = 4200,
            startedAt = System.currentTimeMillis() - (60 * 60 * 1000L),
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80",
            isLive = true,
            isFollowed = true,
            streamTags = listOf("Android", "Coding", "Kotlin", "AMA"),
            language = "English",
            bitRateKbps = 4800,
            fps = 60
        ),
        LiveStream(
            id = "stream_04",
            streamerId = "creator_vikram",
            streamerName = "Vikram Malhotra",
            streamerHandle = "@vikram_fitness",
            streamerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
            title = "Morning Core Workout & High-Energy HIIT Routine ⚡",
            category = StreamCategory.FITNESS,
            viewerCount = 640,
            likesCount = 3100,
            startedAt = System.currentTimeMillis() - (15 * 60 * 1000L),
            thumbnailUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&q=80",
            isLive = true,
            isFollowed = false,
            streamTags = listOf("HIIT", "Cardio", "Health"),
            language = "English",
            bitRateKbps = 3800,
            fps = 30
        ),
        LiveStream(
            id = "stream_05",
            streamerId = "creator_rohan",
            streamerName = "Rohan Das",
            streamerHandle = "@rohan_irl",
            streamerAvatar = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&q=80",
            title = "Exploring Mumbai Night Street Food & Cyber Cafe Crawl 🍜",
            category = StreamCategory.CHATTING,
            viewerCount = 2410,
            likesCount = 11200,
            startedAt = System.currentTimeMillis() - (90 * 60 * 1000L),
            thumbnailUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&q=80",
            isLive = true,
            isFollowed = false,
            streamTags = listOf("IRL", "Travel", "Food", "Vlog"),
            language = "Hindi",
            bitRateKbps = 4200,
            fps = 60
        )
    )

    private val _streams = MutableStateFlow(initialStreams)
    val streams: StateFlow<List<LiveStream>> = _streams.asStateFlow()

    private val _activeBroadcast = MutableStateFlow<LiveStream?>(null)
    val activeBroadcast: StateFlow<LiveStream?> = _activeBroadcast.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<LiveChatMessage>>>(
        mapOf(
            "stream_01" to listOf(
                LiveChatMessage("m1", "stream_01", "u1", "Karan_007", "", "Insane clutch on round 12!! 🔥", badge = ChatBadge.TOP_FAN),
                LiveChatMessage("m2", "stream_01", "u2", "SimranK", "", "Let's go team India! 🇮🇳"),
                LiveChatMessage("m3", "stream_01", "u3", "Mod_Eagle", "", "Welcome everyone! Keep the chat respectful and hype!", badge = ChatBadge.MODERATOR),
                LiveChatMessage("m4", "stream_01", "u4", "Rahul_99", "", "Sent 🌹 Rose x 10!", giftNotice = "Rose 🌹")
            ),
            "stream_02" to listOf(
                LiveChatMessage("m10", "stream_02", "u10", "MusicLover", "", "Could you play 'Fix You' next please? ❤️"),
                LiveChatMessage("m11", "stream_02", "u11", "Deepak_S", "", "Your vocals are angelic tonight!")
            )
        )
    )
    val chatMessages: StateFlow<Map<String, List<LiveChatMessage>>> = _chatMessages.asStateFlow()

    private val _reports = MutableStateFlow<List<StreamReport>>(emptyList())
    val reports: StateFlow<List<StreamReport>> = _reports.asStateFlow()

    fun toggleFollow(streamId: String) {
        _streams.value = _streams.value.map {
            if (it.id == streamId) it.copy(isFollowed = !it.isFollowed) else it
        }
    }

    fun startBroadcast(
        streamer: User,
        title: String,
        category: StreamCategory,
        quality: StreamQuality
    ): LiveStream {
        val newStream = LiveStream(
            id = "broadcast_${UUID.randomUUID().toString().take(8)}",
            streamerId = streamer.id,
            streamerName = streamer.displayName,
            streamerHandle = "@${streamer.username}",
            streamerAvatar = streamer.avatarUrl,
            title = title.ifBlank { "Live with ${streamer.displayName} ⚡" },
            category = category,
            viewerCount = 1,
            likesCount = 0,
            startedAt = System.currentTimeMillis(),
            thumbnailUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&q=80",
            quality = quality,
            isLive = true,
            isFollowed = false,
            streamTags = listOf("Live", category.displayName),
            bitRateKbps = 5200,
            fps = 60
        )
        _activeBroadcast.value = newStream
        _streams.value = listOf(newStream) + _streams.value
        return newStream
    }

    fun stopBroadcast() {
        val active = _activeBroadcast.value ?: return
        _streams.value = _streams.value.map {
            if (it.id == active.id) it.copy(isLive = false) else it
        }
        _activeBroadcast.value = null
    }

    fun sendChatMessage(streamId: String, user: User, text: String, giftNotice: String? = null) {
        val current = _chatMessages.value[streamId] ?: emptyList()
        val badge = if (user.id == _activeBroadcast.value?.streamerId) ChatBadge.STREAMER else ChatBadge.NONE
        val newMsg = LiveChatMessage(
            id = UUID.randomUUID().toString(),
            streamId = streamId,
            userId = user.id,
            userName = user.displayName,
            userAvatar = user.avatarUrl,
            text = text,
            timestamp = System.currentTimeMillis(),
            badge = badge,
            giftNotice = giftNotice
        )
        _chatMessages.value = _chatMessages.value + (streamId to (current + newMsg))
    }

    fun addLike(streamId: String) {
        _streams.value = _streams.value.map {
            if (it.id == streamId) it.copy(likesCount = it.likesCount + 1) else it
        }
    }

    fun reportStream(streamId: String, reportedUserId: String, reason: String, details: String) {
        val report = StreamReport(
            id = UUID.randomUUID().toString(),
            streamId = streamId,
            reportedUserId = reportedUserId,
            reason = reason,
            details = details
        )
        _reports.value = _reports.value + report
    }

    fun terminateStream(streamId: String) {
        _streams.value = _streams.value.map {
            if (it.id == streamId) it.copy(isLive = false) else it
        }
        if (_activeBroadcast.value?.id == streamId) {
            _activeBroadcast.value = null
        }
    }
}
