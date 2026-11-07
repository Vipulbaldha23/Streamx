package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.model.User
import com.example.ui.components.GlassCard
import com.example.ui.components.StreamXTopBar
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    coinBalance: Int,
    unreadNotifications: Int,
    onEditProfile: (String, String) -> Unit,
    onOpenWallet: () -> Unit,
    onOpenCreatorDashboard: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user.displayName) }
    var editBio by remember { mutableStateOf(user.bio) }

    Scaffold(
        topBar = {
            StreamXTopBar(
                coinBalance = coinBalance,
                unreadNotifications = unreadNotifications,
                onWalletClick = onOpenWallet,
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Header Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(86.dp)) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, NeonCyan, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text("⚡", fontSize = 14.sp)
                        }

                        Text(
                            text = "@${user.username}",
                            color = NeonCyan,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = user.bio,
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${user.followersCount}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Followers", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = CardBorderSubtle)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${user.followingCount}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Following", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = CardBorderSubtle)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${user.totalStreams}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Streams", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                editName = user.displayName
                                editBio = user.bio
                                showEditProfileDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile Details", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Hub: Creator Dashboard & Wallet
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenCreatorDashboard)
                            .testTag("hub_creator_dashboard_button")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("📊", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Creator Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Analytics & Payouts", color = TextSecondaryDark, fontSize = 11.sp)
                        }
                    }

                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenWallet)
                            .testTag("hub_wallet_button")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🪙", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Coin Wallet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$coinBalance coins balance", color = GoldCoin, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Settings & Preferences
            item {
                Text("SETTINGS & SAFETY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Dark Theme Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onToggleTheme)
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = NeonCyan)
                                Text("Dark Theme (Liquid Glass)", color = Color.White, fontSize = 14.sp)
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { onToggleTheme() },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF004F54))
                            )
                        }

                        Divider(color = CardBorderSubtle)

                        // Admin Portal Access
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onOpenAdminDashboard)
                                .padding(vertical = 12.dp)
                                .testTag("admin_portal_button"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = ElectricViolet)
                                Column {
                                    Text("Admin & Moderation Portal", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Stream termination, reports & commission", color = TextSecondaryDark, fontSize = 11.sp)
                                }
                            }
                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
                        }

                        Divider(color = CardBorderSubtle)

                        // Logout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onLogout)
                                .padding(vertical = 12.dp)
                                .testTag("profile_logout_button"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Logout, contentDescription = null, tint = StreamPink)
                                Text("Sign Out", color = StreamPink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile Details", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input")
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditProfile(editName, editBio)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
