package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import com.example.model.CreatorEarnings
import com.example.model.KYCStatus
import com.example.model.User
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(
    user: User,
    earnings: CreatorEarnings,
    onRequestPayout: (Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPayoutDialog by remember { mutableStateOf(false) }
    var payoutAmountInput by remember { mutableStateOf("${earnings.pendingPayoutInr.toInt()}") }
    var payoutSuccessMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creator Analytics & Earnings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("creator_dashboard_back_button")) {
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
            // Summary Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF161B30)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CREATOR MONETIZATION", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("KYC VERIFIED ✅", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Pending Payout Balance (INR)", color = TextSecondaryDark, fontSize = 12.sp)
                        Text(
                            text = "₹%.2f".format(earnings.pendingPayoutInr),
                            color = GoldCoin,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Platform split: ${earnings.creatorSharePercent.toInt()}% Creator • ${earnings.platformFeePercent.toInt()}% StreamX Platform fee",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPayoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("request_payout_button")
                        ) {
                            Text("Request Bank Payout (NEFT / IMPS)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            payoutSuccessMsg?.let { msg ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonGreen.copy(alpha = 0.2f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(msg, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Stream Analytics Metrics
            item {
                Text("PERFORMANCE ANALYTICS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Live Streams", color = TextSecondaryDark, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${user.totalStreams}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Past 30 days", color = NeonCyan, fontSize = 10.sp)
                        }
                    }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Peak Concurrent", color = TextSecondaryDark, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("3,840", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("👥 Viewers", color = StreamPink, fontSize = 10.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Broadcast Time", color = TextSecondaryDark, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("78.4 hrs", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Avg 1.8 hrs/session", color = ElectricViolet, fontSize = 10.sp)
                        }
                    }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Virtual Gifts Received", color = TextSecondaryDark, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("1,420 🎁", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("85.2k diamonds", color = GoldCoin, fontSize = 10.sp)
                        }
                    }
                }
            }

            // KYC & Bank Details
            item {
                Text("BANK & COMPLIANCE DETAILS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("PAN Number", color = TextSecondaryDark, fontSize = 13.sp)
                            Text(earnings.panMasked, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Divider(color = CardBorderSubtle)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Settlement Account", color = TextSecondaryDark, fontSize = 13.sp)
                            Text(earnings.bankAccountMasked, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Divider(color = CardBorderSubtle)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TDS / Tax Deductions", color = TextSecondaryDark, fontSize = 13.sp)
                            Text("Compliant under Section 194J", color = NeonGreen, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Payout Dialog
    if (showPayoutDialog) {
        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = { Text("Confirm Payout Request", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Transfer earnings to ${earnings.bankAccountMasked}:", color = TextSecondaryDark, fontSize = 12.sp)
                    OutlinedTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        label = { Text("Amount in INR (₹)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Payouts are settled within 24-48 business hours via NEFT/IMPS.", color = TextMutedDark, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payoutAmountInput.toDoubleOrNull() ?: 0.0
                        onRequestPayout(amount)
                        showPayoutDialog = false
                        payoutSuccessMsg = "✅ Payout request of ₹%.2f initiated successfully!".format(amount)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_payout_button")
                ) {
                    Text("Confirm Transfer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
