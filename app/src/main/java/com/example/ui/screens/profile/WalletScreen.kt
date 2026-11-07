package com.example.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    coinBalance: Int,
    coinPackages: List<CoinPackage>,
    transactions: List<PaymentOrder>,
    onPurchasePackage: (CoinPackage, PaymentGateway) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPackage by remember { mutableStateOf<CoinPackage?>(null) }
    var selectedGateway by remember { mutableStateOf(PaymentGateway.RAZORPAY_UPI) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var lastSuccessOrder by remember { mutableStateOf<PaymentOrder?>(null) }
    var selectedReceiptOrder by remember { mutableStateOf<PaymentOrder?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coin Wallet & Recharge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("wallet_back_button")) {
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
            contentPadding = PaddingValues(16.dp)
        ) {
            // Balance Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF131B2E)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CURRENT COIN BALANCE", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🪙", fontSize = 32.sp)
                            Text(
                                text = "$coinBalance",
                                color = GoldCoin,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text("100 Coins = ₹100 INR Value", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Secured with Razorpay & RBI Compliant Payment Gateway",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Coin Packages Header
            item {
                Text(
                    text = "SELECT RECHARGE PACKAGE (INR ₹)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
            }

            // Coin Packages
            items(coinPackages) { pkg ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            selectedPackage = pkg
                            showCheckoutDialog = true
                        }
                        .testTag("package_${pkg.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🪙", fontSize = 24.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "${pkg.coins} Coins",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (pkg.bonusCoins > 0) {
                                        Text(
                                            text = "+${pkg.bonusCoins} FREE",
                                            color = NeonGreen,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                pkg.tag?.let {
                                    Text(text = it, color = ElectricViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                selectedPackage = pkg
                                showCheckoutDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("buy_package_button_${pkg.id}")
                        ) {
                            Text("₹${pkg.priceInr.toInt()}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Transactions Ledger Header
            item {
                Text(
                    text = "TRANSACTION LEDGER & RECEIPTS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Text("No transactions found yet.", color = TextSecondaryDark, fontSize = 13.sp)
                }
            } else {
                items(transactions) { tx ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedReceiptOrder = tx }
                            .testTag("transaction_${tx.orderId}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Outlined.Receipt, contentDescription = null, tint = NeonCyan)
                                Column {
                                    Text("Order #${tx.orderId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(sdf.format(Date(tx.timestamp)), color = TextSecondaryDark, fontSize = 11.sp)
                                    Text("+${tx.coinsAmount} Coins", color = GoldCoin, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${tx.totalAmountInr}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("VERIFIED ✅", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Checkout Confirmation Dialog
    if (showCheckoutDialog && selectedPackage != null) {
        val pkg = selectedPackage!!
        val gst = pkg.priceInr * 0.18
        val total = pkg.priceInr + gst

        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = {
                Text("Confirm Recharge", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Order Breakdown (Indian Rupees ₹):", color = TextSecondaryDark, fontSize = 12.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Coin Package (${pkg.coins} + ${pkg.bonusCoins})", color = Color.White, fontSize = 13.sp)
                        Text("₹${pkg.priceInr}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST (18% Digital Service Tax)", color = TextSecondaryDark, fontSize = 12.sp)
                        Text("₹%.2f".format(gst), color = TextSecondaryDark, fontSize = 12.sp)
                    }

                    Divider(color = CardBorderSubtle)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payable Amount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("₹%.2f".format(total), color = NeonCyan, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Payment Method:", color = TextSecondaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    PaymentGateway.values().forEach { gateway ->
                        val label = when (gateway) {
                            PaymentGateway.RAZORPAY_UPI -> "UPI (GPay, PhonePe, Paytm, BHIM)"
                            PaymentGateway.RAZORPAY_CARDS -> "Credit / Debit Card (Razorpay)"
                            PaymentGateway.GOOGLE_PLAY_BILLING -> "Google Play In-App Billing"
                            PaymentGateway.NET_BANKING -> "Net Banking (SBI, HDFC, ICICI)"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGateway = gateway }
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedGateway == gateway),
                                onClick = { selectedGateway = gateway },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Text(label, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutDialog = false
                        onPurchasePackage(pkg, selectedGateway)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                    modifier = Modifier.testTag("confirm_payment_button")
                ) {
                    Text("Pay ₹%.2f".format(total), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Receipt details dialog
    if (selectedReceiptOrder != null) {
        val tx = selectedReceiptOrder!!
        AlertDialog(
            onDismissRequest = { selectedReceiptOrder = null },
            title = { Text("Tax Invoice & Receipt", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Invoice No: ${tx.receiptNumber}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Order ID: ${tx.orderId}", color = Color.White, fontSize = 12.sp)
                    Text("Payment Gateway: ${tx.gateway.name}", color = TextSecondaryDark, fontSize = 12.sp)
                    Text("Digital Signature Proof: ${tx.signatureProof}", color = TextMutedDark, fontSize = 10.sp)
                    Divider(color = CardBorderSubtle)
                    Text("Base Price: ₹${tx.amountInr}", color = Color.White, fontSize = 12.sp)
                    Text("18% GST: ₹${tx.gstAmountInr}", color = TextSecondaryDark, fontSize = 12.sp)
                    Text("Total Paid: ₹${tx.totalAmountInr}", color = GoldCoin, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedReceiptOrder = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Close")
                }
            },
            containerColor = SurfaceDark
        )
    }
}
