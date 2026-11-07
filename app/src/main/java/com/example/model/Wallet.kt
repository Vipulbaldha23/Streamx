package com.example.model

data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val priceInr: Double,
    val tag: String? = null,
    val isPopular: Boolean = false
)

enum class PaymentStatus {
    INITIATED,
    PROCESSING,
    VERIFIED_SUCCESS,
    FAILED,
    REFUNDED
}

enum class PaymentGateway {
    RAZORPAY_UPI,
    RAZORPAY_CARDS,
    GOOGLE_PLAY_BILLING,
    NET_BANKING
}

data class PaymentOrder(
    val orderId: String,
    val packageId: String,
    val coinsAmount: Int,
    val amountInr: Double,
    val gstAmountInr: Double,
    val totalAmountInr: Double,
    val gateway: PaymentGateway,
    val status: PaymentStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val signatureProof: String = "",
    val receiptNumber: String = ""
)

enum class KYCStatus {
    NOT_SUBMITTED,
    UNDER_REVIEW,
    VERIFIED,
    REJECTED
}

data class CreatorEarnings(
    val totalCoinsReceived: Long,
    val platformFeePercent: Double = 30.0,
    val creatorSharePercent: Double = 70.0,
    val grossEarningsInr: Double,
    val netPayableInr: Double,
    val pendingPayoutInr: Double,
    val kycStatus: KYCStatus = KYCStatus.VERIFIED,
    val panMasked: String = "ABCDE****F",
    val bankAccountMasked: String = "HDFC Bank •••• 4821"
)
