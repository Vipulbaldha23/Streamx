package com.example.ui.screens.following

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LiveStream
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveBadge
import com.example.ui.components.StreamXTopBar
import com.example.ui.theme.*

@Composable
fun FollowingScreen(
    followedStreams: List<LiveStream>,
    coinBalance: Int,
    unreadNotifications: Int,
    onStreamClick: (LiveStream) -> Unit,
    onToggleFollow: (String) -> Unit,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            StreamXTopBar(
                coinBalance = coinBalance,
                unreadNotifications = unreadNotifications,
                onWalletClick = onWalletClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        containerColor = VoidDark,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "CHANNELS YOU FOLLOW",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (followedStreams.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✨", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Followed Streamers Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Explore live streams and follow creators to get notified whenever they go live!",
                                color = TextSecondaryDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(followedStreams) { stream ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onStreamClick(stream) }
                            .testTag("following_stream_${stream.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(54.dp)) {
                                AsyncImage(
                                    model = stream.streamerAvatar,
                                    contentDescription = stream.streamerName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(2.dp, if (stream.isLive) StreamPink else CardBorderSubtle, CircleShape)
                                )
                                if (stream.isLive) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .offset(y = 4.dp)
                                    ) {
                                        LiveBadge()
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stream.streamerName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = stream.streamerHandle,
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp
                                )
                                if (stream.isLive) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stream.title,
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Follow / Unfollow button
                            OutlinedButton(
                                onClick = { onToggleFollow(stream.id) },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderSubtle),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.05f)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("unfollow_button_${stream.id}")
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Following", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
