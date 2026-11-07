package com.example.ui.screens.broadcast

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.*
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BroadcastScreen(
    currentUser: User,
    activeBroadcast: LiveStream?,
    chatMessages: List<LiveChatMessage>,
    onStartBroadcast: (title: String, category: StreamCategory, quality: StreamQuality) -> Unit,
    onStopBroadcast: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = (permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.RECORD_AUDIO] == true)
    }

    var isFrontCamera by remember { mutableStateOf(true) }
    var isMicMuted by remember { mutableStateOf(false) }
    var streamTitle by remember { mutableStateOf("Streaming live on StreamX ⚡") }
    var selectedCategory by remember { mutableStateOf(StreamCategory.GAMING) }
    var selectedQuality by remember { mutableStateOf(StreamQuality.AUTO) }
    var showEndDialog by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    // Stream duration counter
    var elapsedSeconds by remember { mutableStateOf(0L) }
    LaunchedEffect(activeBroadcast) {
        if (activeBroadcast != null) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
            }
        } else {
            elapsedSeconds = 0L
        }
    }

    if (!hasPermissions) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VoidDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Videocam,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Camera & Audio Permission",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "StreamX requires camera and microphone permissions to broadcast high-definition video and real-time audio to your audience.",
                        color = TextSecondaryDark,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_camera_permission_button")
                    ) {
                        Text("Grant Permissions", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onClose) {
                        Text("Cancel", color = TextSecondaryDark)
                    }
                }
            }
        }
        return
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // Real Camera Preview
        CameraPreview(
            isFrontCamera = isFrontCamera,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent gradient overlay for controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x99000000),
                            Color.Transparent,
                            Color(0xCC000000)
                        )
                    )
                )
        )

        if (activeBroadcast == null) {
            // Pre-broadcast configuration screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    // Camera Switch
                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("switch_camera_pre_broadcast_button")
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = NeonCyan)
                    }
                }

                // Setup Card at Bottom
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xEE0F172A)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "START LIVE BROADCAST",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = streamTitle,
                            onValueChange = { streamTitle = it },
                            label = { Text("Stream Title") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("broadcast_title_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Category",
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(StreamCategory.values().filter { it != StreamCategory.ALL }) { cat ->
                                val isSelected = cat == selectedCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, if (isSelected) NeonCyan else CardBorderSubtle, RoundedCornerShape(12.dp))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${cat.icon} ${cat.displayName}",
                                        color = if (isSelected) Color.White else TextSecondaryDark,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onStartBroadcast(streamTitle, selectedCategory, selectedQuality)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StreamPink, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("go_live_submit_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Text(
                                    text = "GO LIVE NOW",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Live Broadcasting HUD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Live Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveBadge(text = "LIVE")

                        // Viewer counter pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "👥 ${activeBroadcast.viewerCount}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Duration timer
                        val minutes = elapsedSeconds / 60
                        val seconds = elapsedSeconds % 60
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "⏱ %02d:%02d".format(minutes, seconds),
                                color = NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Stop Live Stream
                    Button(
                        onClick = { showEndDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = StreamPink),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("end_stream_button")
                    ) {
                        Text("End Stream", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Middle: Telemetry indicators (FPS, Bitrate, Network health)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("📡 5200 kbps", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("⚡ 60 FPS", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("📶 24 ms (Excellent)", color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom: Real-time chat messages overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        reverseLayout = true
                    ) {
                        items(chatMessages.reversed()) { msg ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${msg.userName}: ",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Streamer Controls Row (Camera flip, mic, quick chat input)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Chat with viewers...", color = TextSecondaryDark, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            trailingIcon = {
                                if (messageText.isNotBlank()) {
                                    IconButton(onClick = {
                                        onSendMessage(messageText)
                                        messageText = ""
                                    }) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        // Camera Switch Icon
                        IconButton(
                            onClick = { isFrontCamera = !isFrontCamera },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .testTag("live_camera_switch_button")
                        ) {
                            Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
                        }

                        // Microphone Mute Toggle
                        IconButton(
                            onClick = { isMicMuted = !isMicMuted },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isMicMuted) StreamPink else Color.Black.copy(alpha = 0.6f))
                                .testTag("live_mic_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Toggle Mic",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // End stream confirmation dialog
    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("End Live Stream?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to finish broadcasting? Your live stats will be saved to your Creator Dashboard.", color = TextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        showEndDialog = false
                        onStopBroadcast()
                        showSummary = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreamPink)
                ) {
                    Text("End Broadcast", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text("Stay Live", color = Color.White)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Post-stream summary dialog
    if (showSummary) {
        AlertDialog(
            onDismissRequest = {
                showSummary = false
                onClose()
            },
            title = {
                Text("Broadcast Summary 🎉", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Great stream! Here are your session metrics:", color = TextSecondaryDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("⏱ Duration: %02d min %02d sec".format(elapsedSeconds / 60, elapsedSeconds % 60), color = Color.White)
                    Text("👥 Peak Concurrent Viewers: 142", color = NeonCyan, fontWeight = FontWeight.Bold)
                    Text("🪙 Diamonds / Coins Earned: 850 💎", color = GoldCoin, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSummary = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            },
            containerColor = SurfaceDark
        )
    }
}
