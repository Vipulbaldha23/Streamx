package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VirtualGift
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay

@Composable
fun GiftOverlay(
    activeGift: VirtualGift?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGift == null) return

    LaunchedEffect(activeGift) {
        delay(activeGift.effectDurationMs)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gift_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gift_pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xEE0F172A),
                            Color(0xEE1E1B4B)
                        )
                    )
                )
                .border(
                    2.dp,
                    Brush.linearGradient(listOf(NeonCyan, ElectricViolet, GoldCoin)),
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeGift.iconEmoji,
                    fontSize = 54.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SUPER GIFT!",
                color = GoldCoin,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = activeGift.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🪙", fontSize = 16.sp)
                Text(
                    text = "${activeGift.coinCost} Coins Sent",
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
