package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StreamPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FloatingHeart(
    val id: Long,
    val color: Color,
    val startXOffset: Float,
    val size: Float
)

@Composable
fun FloatingReactionsContainer(
    triggerCount: Int,
    modifier: Modifier = Modifier
) {
    val hearts = remember { mutableStateListOf<FloatingHeart>() }
    val colors = listOf(StreamPink, NeonCyan, ElectricViolet, Color(0xFFFF5252), Color(0xFFFF4081))

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            val heart = FloatingHeart(
                id = System.nanoTime(),
                color = colors.random(),
                startXOffset = (Random.nextFloat() - 0.5f) * 120f,
                size = Random.nextFloat() * 14f + 24f
            )
            hearts.add(heart)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        hearts.forEach { heart ->
            key(heart.id) {
                SingleAnimatedHeart(
                    heart = heart,
                    onFinished = { hearts.remove(heart) }
                )
            }
        }
    }
}

@Composable
private fun SingleAnimatedHeart(
    heart: FloatingHeart,
    onFinished: () -> Unit
) {
    val yOffset = remember { Animatable(0f) }
    val xWiggle = remember { Animatable(heart.startXOffset) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1.2f, tween(200))
            scale.animateTo(1.0f, tween(300))
        }
        launch {
            yOffset.animateTo(-600f, tween(1800, easing = FastOutLinearInEasing))
        }
        launch {
            xWiggle.animateTo(heart.startXOffset + (Random.nextFloat() - 0.5f) * 60f, tween(900))
            xWiggle.animateTo(heart.startXOffset + (Random.nextFloat() - 0.5f) * 90f, tween(900))
        }
        launch {
            delay(1000)
            alpha.animateTo(0f, tween(800))
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heart.color,
            modifier = Modifier
                .offset { IntOffset(xWiggle.value.toInt() - 60, yOffset.value.toInt() - 80) }
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}
