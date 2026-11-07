package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StreamRepository
import com.example.data.WalletRepository
import com.example.model.PaymentGateway
import com.example.model.User
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StreamX Live", appName)
  }

  @Test
  fun `verify initial streams exist`() {
    val streamRepo = StreamRepository()
    val list = streamRepo.streams.value
    assertTrue("Initial stream feed should not be empty", list.isNotEmpty())
    assertTrue("At least one stream is live", list.any { it.isLive })
  }

  @Test
  fun `verify coin wallet recharge and gift deduction`() {
    val walletRepo = WalletRepository()
    val initialBalance = walletRepo.coinBalance.value

    // Purchase package of 100 coins
    val pkg = walletRepo.coinPackages.first()
    walletRepo.purchaseCoinPackage(
      pkg = pkg,
      gateway = PaymentGateway.RAZORPAY_UPI,
      onSuccess = {},
      onFailure = {}
    )

    assertEquals(initialBalance + pkg.coins, walletRepo.coinBalance.value)

    // Send a 10 coin Rose
    val rose = walletRepo.virtualGifts.first { it.coinCost == 10 }
    val dummyUser = User("u_test", "tester", "Tester", "")
    walletRepo.sendGift(
      gift = rose,
      streamerId = "creator_raj",
      senderUser = dummyUser,
      onSuccess = {},
      onInsufficientFunds = {}
    )

    assertEquals(initialBalance + pkg.coins - 10, walletRepo.coinBalance.value)
  }
}
