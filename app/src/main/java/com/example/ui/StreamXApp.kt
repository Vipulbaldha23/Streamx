package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdminRepository
import com.example.data.AuthRepository
import com.example.data.StreamRepository
import com.example.data.WalletRepository
import com.example.model.*
import com.example.ui.components.GiftOverlay
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.broadcast.BroadcastScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.following.FollowingScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.player.LivePlayerScreen
import com.example.ui.screens.profile.CreatorDashboardScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.WalletScreen
import com.example.ui.theme.*

enum class AppScreen {
    HOME,
    EXPLORE,
    CREATE_LIVE,
    FOLLOWING,
    PROFILE,
    LIVE_PLAYER,
    WALLET,
    CREATOR_DASHBOARD,
    NOTIFICATIONS,
    ADMIN_DASHBOARD
}

@Composable
fun StreamXApp(
    authRepo: AuthRepository = remember { AuthRepository() },
    streamRepo: StreamRepository = remember { StreamRepository() },
    walletRepo: WalletRepository = remember { WalletRepository() },
    adminRepo: AdminRepository = remember { AdminRepository() },
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val authState by authRepo.authState.collectAsState()
    val streams by streamRepo.streams.collectAsState()
    val activeBroadcast by streamRepo.activeBroadcast.collectAsState()
    val chatMessagesMap by streamRepo.chatMessages.collectAsState()
    val reports by streamRepo.reports.collectAsState()

    val coinBalance by walletRepo.coinBalance.collectAsState()
    val transactions by walletRepo.transactions.collectAsState()
    val creatorEarnings by walletRepo.creatorEarnings.collectAsState()
    val activeGiftAnimation by walletRepo.activeGiftAnimation.collectAsState()
    val adminSettings by adminRepo.settings.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var previousScreen by remember { mutableStateOf(AppScreen.HOME) }
    var activeWatchingStream by remember { mutableStateOf<LiveStream?>(null) }

    val notifications = remember {
        mutableStateListOf(
            AppNotification("n1", "Aarav Sharma is LIVE!", "Grand Championship Finals stream just started 🎮", NotificationType.LIVE_STARTED, targetStreamId = "stream_01"),
            AppNotification("n2", "Gift Received! 🌹", "Karan_007 sent you 10 Roses in your last stream", NotificationType.GIFT_RECEIVED),
            AppNotification("n3", "Recharge Successful", "Added 550 coins via Razorpay UPI #ORD_RZP_98241", NotificationType.PAYMENT_SUCCESS),
            AppNotification("n4", "New Follower", "SimranK started following your stream channel!", NotificationType.NEW_FOLLOWER)
        )
    }

    val unreadNotifsCount = notifications.count { !it.isRead }

    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            previousScreen = currentScreen
            currentScreen = screen
        }
    }

    // Handle Android system Back Button
    BackHandler(enabled = (currentScreen != AppScreen.HOME)) {
        if (currentScreen == AppScreen.LIVE_PLAYER) {
            activeWatchingStream = null
            currentScreen = previousScreen
        } else {
            currentScreen = AppScreen.HOME
        }
    }

    // Authenticated User check
    val currentUser = (authState as? AuthState.Authenticated)?.user

    if (currentUser == null) {
        AuthScreen(
            onLoginSuccess = { email, pass ->
                authRepo.login(email, pass)
            },
            onRegisterSuccess = { name, email, pass ->
                authRepo.register(name, email, pass)
            },
            onGoogleSignIn = {
                authRepo.googleSignIn()
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(VoidDark)) {
        Scaffold(
            bottomBar = {
                // Show bottom navigation bar only on primary tabs
                val isPrimaryTab = currentScreen in listOf(
                    AppScreen.HOME,
                    AppScreen.EXPLORE,
                    AppScreen.FOLLOWING,
                    AppScreen.PROFILE
                )
                if (isPrimaryTab) {
                    StreamXBottomNav(
                        currentScreen = currentScreen,
                        onTabSelected = { selected ->
                            if (selected == AppScreen.CREATE_LIVE) {
                                navigateTo(AppScreen.CREATE_LIVE)
                            } else {
                                navigateTo(selected)
                            }
                        }
                    )
                }
            },
            containerColor = VoidDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = if (currentScreen in listOf(
                                AppScreen.HOME,
                                AppScreen.EXPLORE,
                                AppScreen.FOLLOWING,
                                AppScreen.PROFILE
                            )
                        ) 0.dp else innerPadding.calculateBottomPadding()
                    )
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            streams = streams,
                            coinBalance = coinBalance,
                            unreadNotifications = unreadNotifsCount,
                            onStreamClick = { stream ->
                                activeWatchingStream = stream
                                navigateTo(AppScreen.LIVE_PLAYER)
                            },
                            onWalletClick = { navigateTo(AppScreen.WALLET) },
                            onNotificationsClick = { navigateTo(AppScreen.NOTIFICATIONS) },
                            onSearchClick = { navigateTo(AppScreen.EXPLORE) }
                        )
                    }

                    AppScreen.EXPLORE -> {
                        ExploreScreen(
                            streams = streams,
                            coinBalance = coinBalance,
                            unreadNotifications = unreadNotifsCount,
                            onStreamClick = { stream ->
                                activeWatchingStream = stream
                                navigateTo(AppScreen.LIVE_PLAYER)
                            },
                            onWalletClick = { navigateTo(AppScreen.WALLET) },
                            onNotificationsClick = { navigateTo(AppScreen.NOTIFICATIONS) }
                        )
                    }

                    AppScreen.CREATE_LIVE -> {
                        val broadcastMessages = chatMessagesMap[activeBroadcast?.id ?: ""] ?: emptyList()
                        BroadcastScreen(
                            currentUser = currentUser,
                            activeBroadcast = activeBroadcast,
                            chatMessages = broadcastMessages,
                            onStartBroadcast = { title, cat, qual ->
                                streamRepo.startBroadcast(currentUser, title, cat, qual)
                            },
                            onStopBroadcast = {
                                streamRepo.stopBroadcast()
                            },
                            onSendMessage = { text ->
                                activeBroadcast?.let { b ->
                                    streamRepo.sendChatMessage(b.id, currentUser, text)
                                }
                            },
                            onClose = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }

                    AppScreen.FOLLOWING -> {
                        val followed = streams.filter { it.isFollowed }
                        FollowingScreen(
                            followedStreams = followed,
                            coinBalance = coinBalance,
                            unreadNotifications = unreadNotifsCount,
                            onStreamClick = { stream ->
                                activeWatchingStream = stream
                                navigateTo(AppScreen.LIVE_PLAYER)
                            },
                            onToggleFollow = { streamId ->
                                streamRepo.toggleFollow(streamId)
                            },
                            onWalletClick = { navigateTo(AppScreen.WALLET) },
                            onNotificationsClick = { navigateTo(AppScreen.NOTIFICATIONS) }
                        )
                    }

                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            user = currentUser,
                            coinBalance = coinBalance,
                            unreadNotifications = unreadNotifsCount,
                            onEditProfile = { name, bio ->
                                authRepo.updateProfile(name, bio)
                            },
                            onOpenWallet = { navigateTo(AppScreen.WALLET) },
                            onOpenCreatorDashboard = { navigateTo(AppScreen.CREATOR_DASHBOARD) },
                            onOpenAdminDashboard = { navigateTo(AppScreen.ADMIN_DASHBOARD) },
                            onNotificationsClick = { navigateTo(AppScreen.NOTIFICATIONS) },
                            onLogout = { authRepo.logout() },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )
                    }

                    AppScreen.LIVE_PLAYER -> {
                        val stream = activeWatchingStream ?: streams.first()
                        val msgs = chatMessagesMap[stream.id] ?: emptyList()
                        LivePlayerScreen(
                            stream = stream,
                            currentUser = currentUser,
                            chatMessages = msgs,
                            virtualGifts = walletRepo.virtualGifts,
                            userCoinBalance = coinBalance,
                            onBack = {
                                activeWatchingStream = null
                                navigateTo(AppScreen.HOME)
                            },
                            onFollowClick = {
                                streamRepo.toggleFollow(stream.id)
                            },
                            onSendMessage = { text ->
                                streamRepo.sendChatMessage(stream.id, currentUser, text)
                            },
                            onSendGift = { gift ->
                                walletRepo.sendGift(
                                    gift = gift,
                                    streamerId = stream.streamerId,
                                    senderUser = currentUser,
                                    onSuccess = {
                                        streamRepo.sendChatMessage(
                                            streamId = stream.id,
                                            user = currentUser,
                                            text = "Sent ${gift.iconEmoji} ${gift.name}!",
                                            giftNotice = "${gift.name} ${gift.iconEmoji}"
                                        )
                                    },
                                    onInsufficientFunds = {
                                        navigateTo(AppScreen.WALLET)
                                    }
                                )
                            },
                            onRechargeClick = { navigateTo(AppScreen.WALLET) },
                            onReportStream = { reason, details ->
                                streamRepo.reportStream(stream.id, stream.streamerId, reason, details)
                            }
                        )
                    }

                    AppScreen.WALLET -> {
                        WalletScreen(
                            coinBalance = coinBalance,
                            coinPackages = walletRepo.coinPackages,
                            transactions = transactions,
                            onPurchasePackage = { pkg, gateway ->
                                walletRepo.purchaseCoinPackage(
                                    pkg = pkg,
                                    gateway = gateway,
                                    onSuccess = { order ->
                                        notifications.add(
                                            0,
                                            AppNotification(
                                                id = "pay_${order.orderId}",
                                                title = "Recharge Successful 🎉",
                                                message = "Added ${order.coinsAmount} coins via ${order.gateway.name}",
                                                type = NotificationType.PAYMENT_SUCCESS
                                            )
                                        )
                                    },
                                    onFailure = {}
                                )
                            },
                            onBack = { navigateTo(previousScreen) }
                        )
                    }

                    AppScreen.CREATOR_DASHBOARD -> {
                        CreatorDashboardScreen(
                            user = currentUser,
                            earnings = creatorEarnings,
                            onRequestPayout = { amount ->
                                walletRepo.requestPayout(
                                    amountInr = amount,
                                    onSuccess = {},
                                    onError = {}
                                )
                            },
                            onBack = { navigateTo(AppScreen.PROFILE) }
                        )
                    }

                    AppScreen.NOTIFICATIONS -> {
                        NotificationsScreen(
                            notifications = notifications,
                            onNotificationClick = { notif ->
                                notif.targetStreamId?.let { sId ->
                                    streams.find { it.id == sId }?.let { s ->
                                        activeWatchingStream = s
                                        navigateTo(AppScreen.LIVE_PLAYER)
                                    }
                                }
                            },
                            onBack = { navigateTo(previousScreen) }
                        )
                    }

                    AppScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            activeStreams = streams.filter { it.isLive },
                            reports = reports,
                            adminSettings = adminSettings,
                            onTerminateStream = { sId ->
                                streamRepo.terminateStream(sId)
                            },
                            onBanUser = { uId ->
                                adminRepo.banUser(uId)
                            },
                            onUpdateSettings = { comm, rate, minP ->
                                adminRepo.updateSettings(comm, rate, minP)
                            },
                            onBack = { navigateTo(AppScreen.PROFILE) }
                        )
                    }
                }
            }
        }

        // Global Explosive Virtual Gift Animation Overlay
        GiftOverlay(
            activeGift = activeGiftAnimation,
            onDismiss = { walletRepo.dismissGiftAnimation() }
        )
    }
}

@Composable
fun StreamXBottomNav(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Liquid Glass Floating Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xE60E1424))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.3f), Color.White.copy(alpha = 0.08f), ElectricViolet.copy(alpha = 0.3f))),
                    RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            BottomNavItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "Home",
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onTabSelected(AppScreen.HOME) },
                testTag = "nav_home_button"
            )

            // 2. Explore
            BottomNavItem(
                icon = Icons.Outlined.Explore,
                selectedIcon = Icons.Filled.Explore,
                label = "Explore",
                isSelected = currentScreen == AppScreen.EXPLORE,
                onClick = { onTabSelected(AppScreen.EXPLORE) },
                testTag = "nav_explore_button"
            )

            // 3. Create Live Center Action Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(NeonCyan, StreamPink))
                    )
                    .clickable { onTabSelected(AppScreen.CREATE_LIVE) }
                    .testTag("nav_create_live_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Create Live",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 4. Following
            BottomNavItem(
                icon = Icons.Outlined.People,
                selectedIcon = Icons.Filled.People,
                label = "Following",
                isSelected = currentScreen == AppScreen.FOLLOWING,
                onClick = { onTabSelected(AppScreen.FOLLOWING) },
                testTag = "nav_following_button"
            )

            // 5. Profile
            BottomNavItem(
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                label = "Profile",
                isSelected = currentScreen == AppScreen.PROFILE,
                onClick = { onTabSelected(AppScreen.PROFILE) },
                testTag = "nav_profile_button"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else icon,
            contentDescription = label,
            tint = if (isSelected) NeonCyan else TextSecondaryDark,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NeonCyan else TextSecondaryDark,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
