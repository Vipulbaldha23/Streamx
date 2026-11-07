package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdminSettings
import com.example.model.LiveStream
import com.example.model.StreamReport
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    activeStreams: List<LiveStream>,
    reports: List<StreamReport>,
    adminSettings: AdminSettings,
    onTerminateStream: (String) -> Unit,
    onBanUser: (String) -> Unit,
    onUpdateSettings: (Double, Double, Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commissionInput by remember { mutableStateOf("${adminSettings.platformCommissionPercent.toInt()}") }
    var coinRateInput by remember { mutableStateOf("${adminSettings.coinConversionRateInr}") }
    var minPayoutInput by remember { mutableStateOf("${adminSettings.minimumPayoutInr.toInt()}") }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StreamX Admin & Moderation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidDark)
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
            // Admin Warning Pill
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A1526))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = StreamPink)
                    Text(
                        "Elevated Privileges: Actions immediately affect production live streams & user trust.",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // Platform Economics Settings Card
            item {
                Text("PLATFORM REVENUE & MONETIZATION SETTINGS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = commissionInput,
                            onValueChange = { commissionInput = it },
                            label = { Text("Platform Fee % (e.g. 30%)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderSubtle,
                                focusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("admin_commission_input")
                        )

                        OutlinedTextField(
                            value = coinRateInput,
                            onValueChange = { coinRateInput = it },
                            label = { Text("INR per Coin Ratio (1 Coin = ₹X)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderSubtle,
                                focusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = minPayoutInput,
                            onValueChange = { minPayoutInput = it },
                            label = { Text("Minimum Payout Threshold (₹ INR)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorderSubtle,
                                focusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val comm = commissionInput.toDoubleOrNull() ?: 30.0
                                val rate = coinRateInput.toDoubleOrNull() ?: 1.0
                                val minP = minPayoutInput.toDoubleOrNull() ?: 500.0
                                onUpdateSettings(comm, rate, minP)
                                savedMessage = "✅ Settings saved & applied across platform backend!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("save_admin_settings_button")
                        ) {
                            Text("Update Platform Rules", fontWeight = FontWeight.Bold)
                        }

                        savedMessage?.let {
                            Text(it, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Live Stream Monitoring & Termination Queue
            item {
                Text("ACTIVE LIVE BROADCASTS MONITOR (${activeStreams.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            if (activeStreams.isEmpty()) {
                item {
                    Text("No live streams broadcasting currently.", color = TextSecondaryDark, fontSize = 13.sp)
                }
            } else {
                items(activeStreams) { stream ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    LiveBadge()
                                    Text(stream.streamerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(stream.title, color = TextSecondaryDark, fontSize = 12.sp, maxLines = 1)
                                Text("ID: ${stream.id} • ${stream.viewerCount} Viewers", color = NeonCyan, fontSize = 11.sp)
                            }

                            // Terminate Stream Button
                            Button(
                                onClick = { onTerminateStream(stream.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = StreamPink),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("terminate_stream_${stream.id}")
                            ) {
                                Icon(Icons.Outlined.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Terminate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Safety Reports Queue
            item {
                Text("USER REPORTS QUEUE (${reports.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            if (reports.isEmpty()) {
                item {
                    Text("No flagged reports in queue. Stream ecosystem is clear! 🛡️", color = TextSecondaryDark, fontSize = 13.sp)
                }
            } else {
                items(reports) { r ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Reason: ${r.reason}", color = StreamPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Button(
                                    onClick = { onBanUser(r.reportedUserId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Ban User", fontSize = 10.sp)
                                }
                            }
                            if (r.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Details: ${r.details}", color = TextSecondaryDark, fontSize = 12.sp)
                            }
                            Text("Target Stream: ${r.streamId}", color = TextMutedDark, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
