package com.arb.app.photoeffect.ui.commonViews

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arb.app.photoeffect.ui.theme.DarkBlue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun LoadingOverlay(isVisible: Boolean) {
    if (isVisible) {
        Box(
            Modifier
                .wrapContentSize()
                .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
                .padding(30.dp),
            contentAlignment = Alignment.Center,
        ) {
            CustomProgressIndicator()
        }
    }
}

@Composable
fun CustomProgressIndicator(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    circleDiameter: Dp = 20.dp,
    dotSize: Dp = 12.dp,
    animationDuration: Int = 2000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")

    val angles = List(dotCount) { index ->
        val delay = index * (animationDuration / dotCount)
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = animationDuration, easing = LinearEasing),
                initialStartOffset = StartOffset(delay)
            ),
            label = "angle$index"
        )
    }

    Layout(
        modifier = modifier.size(circleDiameter),
        content = {
            repeat(dotCount) { index ->
                val angle = angles[index].value
                val alpha = ((sin(angle) + 1f) / 2f).coerceIn(0.3f, 1f)

                Box(
                    Modifier
                        .size(dotSize)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(DarkBlue)
                )
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val radius = constraints.maxWidth / 2f
            placeables.forEachIndexed { index, placeable ->
                val angle = angles[index].value.toDouble()
                val x = (radius + radius * sin(angle) - placeable.width / 2).roundToInt()
                val y = (radius - radius * cos(angle) - placeable.height / 2).roundToInt()
                placeable.place(x, y)
            }
        }
    }
}