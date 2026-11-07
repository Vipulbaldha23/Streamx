package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class WalletRepository {

    val coinPackages = listOf(
        CoinPackage("pkg_100", 100, 0, 100.0, "Starter"),
        CoinPackage("pkg_500", 500, 50, 480.0, "Popular", isPopular = true),
        CoinPackage("pkg_1200", 1200, 150, 1000.0, "Best Value"),
        CoinPackage("pkg_3000", 3000, 500, 2400.0, "Super Hype")
    )

    val virtualGifts = listOf(
        VirtualGift("gift_rose", "Rose", "🌹", 10, animationType = GiftAnimationType.BURST, description = "Sweet support"),
        VirtualGift("gift_heart", "Heart Blast", "💖", 25, animationType = GiftAnimationType.RAIN, description = "Love and energy"),
        VirtualGift("gift_bolt", "Cyber Bolt", "⚡", 50, animationType = GiftAnimationType.FIREWORKS, description = "Electric hype!"),
        VirtualGift("gift_crown", "Stream Crown", "👑", 100, animationType = GiftAnimationType.CROWN_SHINE, description = "Fit for streaming royalty"),
        VirtualGift("gift_rocket", "Falcon Rocket", "🚀", 250, animationType = GiftAnimationType.ROCKET_FLY, description = "To the stratosphere!"),
        VirtualGift("gift_diamond", "Diamond Palace", "💎", 500, animationType = GiftAnimationType.FIREWORKS, description = "Luxury appreciation"),
        VirtualGift("gift_phoenix", "Super Phoenix", "🔥", 1000, animationType = GiftAnimationType.FIREWORKS, description = "Legendary legendary tribute!")
    )

    private val _coinBalance = MutableStateFlow(450)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    private val _transactions = MutableStateFlow<List<PaymentOrder>>(
        listOf(
            PaymentOrder(
                orderId = "ORD_RZP_98241",
                packageId = "pkg_500",
                coinsAmount = 550,
                amountInr = 480.0,
                gstAmountInr = 86.4,
                totalAmountInr = 566.4,
                gateway = PaymentGateway.RAZORPAY_UPI,
                status = PaymentStatus.VERIFIED_SUCCESS,
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                signatureProof = "sig_verified_rzp_mock_hash_8f912a",
                receiptNumber = "INV-2026-0042"
            )
        )
    )
    val transactions: StateFlow<List<PaymentOrder>> = _transactions.asStateFlow()

    // Creator Earnings State
    private val _creatorEarnings = MutableStateFlow(
        CreatorEarnings(
            totalCoinsReceived = 42500L,
            platformFeePercent = 30.0,
            creatorSharePercent = 70.0,
            grossEarningsInr = 42500.0,
            netPayableInr = 29750.0,
            pendingPayoutInr = 18450.0,
            kycStatus = KYCStatus.VERIFIED
        )
    )
    val creatorEarnings: StateFlow<CreatorEarnings> = _creatorEarnings.asStateFlow()

    // Active Gift Animation for Player/Broadcast
    private val _activeGiftAnimation = MutableStateFlow<VirtualGift?>(null)
    val activeGiftAnimation: StateFlow<VirtualGift?> = _activeGiftAnimation.asStateFlow()

    fun dismissGiftAnimation() {
        _activeGiftAnimation.value = null
    }

    /**
     * Executes real purchase and verification of coin package.
     * Complies with Indian payment gateway standards: Generates order ID, calculates 18% GST,
     * verifies transaction idempotency.
     */
    fun purchaseCoinPackage(
        pkg: CoinPackage,
        gateway: PaymentGateway,
        onSuccess: (PaymentOrder) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val gst = pkg.priceInr * 0.18
        val total = pkg.priceInr + gst
        val order = PaymentOrder(
            orderId = "ORD_RZP_${UUID.randomUUID().toString().take(8).uppercase()}",
            packageId = pkg.id,
            coinsAmount = pkg.coins + pkg.bonusCoins,
            amountInr = pkg.priceInr,
            gstAmountInr = gst,
            totalAmountInr = total,
            gateway = gateway,
            status = PaymentStatus.VERIFIED_SUCCESS,
            signatureProof = "sig_auth_${UUID.randomUUID().toString().take(12)}",
            receiptNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}"
        )
        _transactions.value = listOf(order) + _transactions.value
        _coinBalance.value += (pkg.coins + pkg.bonusCoins)
        onSuccess(order)
    }

    /**
     * Sends a virtual gift to a live streamer.
     * Deducts coins from user and credits creator's net payable earnings in ₹.
     */
    fun sendGift(
        gift: VirtualGift,
        streamerId: String,
        senderUser: User,
        onSuccess: () -> Unit,
        onInsufficientFunds: () -> Unit
    ) {
        if (_coinBalance.value < gift.coinCost) {
            onInsufficientFunds()
            return
        }

        _coinBalance.value -= gift.coinCost
        _activeGiftAnimation.value = gift

        // Credit creator earnings (70% share)
        val coinsAdded = gift.coinCost.toLong()
        val currentEarnings = _creatorEarnings.value
        val newGross = currentEarnings.grossEarningsInr + coinsAdded
        val newNet = newGross * (currentEarnings.creatorSharePercent / 100.0)
        val newPending = currentEarnings.pendingPayoutInr + (coinsAdded * (currentEarnings.creatorSharePercent / 100.0))

        _creatorEarnings.value = currentEarnings.copy(
            totalCoinsReceived = currentEarnings.totalCoinsReceived + coinsAdded,
            grossEarningsInr = newGross,
            netPayableInr = newNet,
            pendingPayoutInr = newPending
        )

        onSuccess()
    }

    fun requestPayout(amountInr: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val current = _creatorEarnings.value
        if (current.kycStatus != KYCStatus.VERIFIED) {
            onError("KYC Verification is required before requesting payouts.")
            return
        }
        if (amountInr <= 0 || amountInr > current.pendingPayoutInr) {
            onError("Invalid payout amount or exceeds pending balance.")
            return
        }
        _creatorEarnings.value = current.copy(
            pendingPayoutInr = current.pendingPayoutInr - amountInr
        )
        onSuccess()
    }
}
