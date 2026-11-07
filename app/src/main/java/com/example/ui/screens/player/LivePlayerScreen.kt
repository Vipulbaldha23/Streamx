package com.example.ui.screens.player

import android.app.Activity
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.FloatingReactionsContainer
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePlayerScreen(
    stream: LiveStream,
    currentUser: User,
    chatMessages: List<LiveChatMessage>,
    virtualGifts: List<VirtualGift>,
    userCoinBalance: Int,
    onBack: () -> Unit,
    onFollowClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendGift: (VirtualGift) -> Unit,
    onRechargeClick: () -> Unit,
    onReportStream: (reason: String, details: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var heartTapCount by remember { mutableStateOf(0) }
    var chatInput by remember { mutableStateOf("") }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf(stream.quality) }
    var isMuted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        heartTapCount++
                    }
                )
            }
    ) {
        // Stream Video Canvas Simulation with Animated Atmosphere & High-res Stream Source
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = stream.thumbnailUrl,
                contentDescription = stream.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic stream lighting & dark translucent gradients for chat contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x99000000),
                                Color.Transparent,
                                Color(0xD0000000)
                            )
                        )
                    )
            )
        }

        // Floating Hearts Particle System
        FloatingReactionsContainer(
            triggerCount = heartTapCount,
            modifier = Modifier.fillMaxSize()
        )

        // Main Player Overlay Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streamer Info Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp).testTag("player_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    AsyncImage(
                        model = stream.streamerAvatar,
                        contentDescription = stream.streamerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, NeonCyan, CircleShape)
                    )

                    Column {
                        Text(
                            text = stream.streamerName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            LiveBadge()
                            Text(
                                text = "👥 ${stream.viewerCount}",
                                color = TextSecondaryDark,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Follow Button
                    Button(
                        onClick = onFollowClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (stream.isFollowed) Color.White.copy(alpha = 0.2f) else NeonCyan,
                            contentColor = if (stream.isFollowed) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("player_follow_button")
                    ) {
                        Text(
                            text = if (stream.isFollowed) "Following" else "+ Follow",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Actions: PiP, Quality, Report
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Picture in Picture
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                (context as? Activity)?.enterPictureInPictureMode()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("player_pip_button")
                    ) {
                        Icon(Icons.Default.PictureInPictureAlt, contentDescription = "PiP", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Quality Selector
                    IconButton(
                        onClick = { showQualityDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("player_quality_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Report Stream
                    IconButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("player_report_button")
                    ) {
                        Icon(Icons.Outlined.Report, contentDescription = "Report", tint = StreamPink, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Live Chat Feed Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true
                ) {
                    items(chatMessages.reversed()) { message ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x990E1424))
                                .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (message.badge == ChatBadge.MODERATOR) {
                                Text("🛡️ ", fontSize = 11.sp)
                            } else if (message.badge == ChatBadge.TOP_FAN) {
                                Text("⭐ ", fontSize = 11.sp)
                            }

                            Text(
                                text = "${message.userName}: ",
                                color = if (message.badge == ChatBadge.MODERATOR) NeonCyan else Color(0xFF7DD3FC),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Interaction Row: Chat input + Gift button + Like button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Say something nice...", color = TextSecondaryDark, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xCC0E1424),
                            unfocusedContainerColor = Color(0x990E1424),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            if (chatInput.isNotBlank()) {
                                IconButton(onClick = {
                                    onSendMessage(chatInput)
                                    chatInput = ""
                                }) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("player_chat_input")
                    )

                    // Gift Button (Glowing Gold)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(GoldCoin, Color(0xFFFF8800)))
                            )
                            .clickable { showGiftSheet = true }
                            .testTag("player_open_gifts_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎁", fontSize = 22.sp)
                    }

                    // Like Reaction Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(StreamPink)
                            .clickable { heartTapCount++ }
                            .testTag("player_like_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.White)
                    }
                }
            }
        }
    }

    // Gift Selection Bottom Sheet
    if (showGiftSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftSheet = false },
            containerColor = Color(0xFF0F172A),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Send Virtual Gift", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Support ${stream.streamerName}", color = TextSecondaryDark, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                showGiftSheet = false
                                onRechargeClick()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🪙", fontSize = 14.sp)
                        Text("$userCoinBalance", color = GoldCoin, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("+ Recharge", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Gifts Grid
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(virtualGifts) { gift ->
                        val canAfford = userCoinBalance >= gift.coinCost
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E2638))
                                .border(1.dp, if (canAfford) CardBorderDark else CardBorderSubtle, RoundedCornerShape(16.dp))
                                .clickable {
                                    showGiftSheet = false
                                    onSendGift(gift)
                                }
                                .padding(10.dp)
                                .testTag("gift_item_${gift.id}")
                        ) {
                            Text(text = gift.iconEmoji, fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${gift.coinCost} Coins",
                                color = if (canAfford) GoldCoin else TextMutedDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Stream Quality Dialog
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Stream Settings", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Playback Quality:", color = TextSecondaryDark, fontSize = 13.sp)
                    StreamQuality.values().forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (quality == selectedQuality) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    selectedQuality = quality
                                    showQualityDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(quality.label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(quality.bitrate, color = TextSecondaryDark, fontSize = 11.sp)
                            }
                            if (quality == selectedQuality) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("Done", color = NeonCyan)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Report Stream Dialog
    if (showReportDialog) {
        var reportReason by remember { mutableStateOf("Inappropriate Content") }
        var reportDetails by remember { mutableStateOf("") }
        val reasons = listOf("Inappropriate Content", "Spam / Scam", "Copyright Violation", "Harassment")

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Live Stream", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Help keep StreamX safe. Select reason:", color = TextSecondaryDark, fontSize = 12.sp)
                    reasons.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (reportReason == r),
                                onClick = { reportReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = StreamPink)
                            )
                            Text(r, color = Color.White, fontSize = 13.sp)
                        }
                    }
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        placeholder = { Text("Additional details (optional)", color = TextSecondaryDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StreamPink,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportStream(reportReason, reportDetails)
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreamPink)
                ) {
                    Text("Submit Report", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
