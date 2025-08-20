package com.arb.app.photoeffect.ui.commonViews

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun MorphingDropSpinnerOverlay(
    visible: Boolean,
    size: Dp = 48.dp,
    travel: Dp = 140.dp,
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .height(travel + size)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val transition = rememberInfiniteTransition(label = "loading")

                val progress by transition.animateFloat(
                    0f, 1f,
                    animationSpec = infiniteRepeatable(
                        tween(900, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "progress"
                )

                val offsetProgress by transition.animateFloat(
                    0f, 1f,
                    animationSpec = infiniteRepeatable(
                        tween(900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "offset"
                )

                val rotation by transition.animateFloat(
                    0f, 360f,
                    animationSpec = infiniteRepeatable(
                        tween(900, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )

                val circleCorner = size / 2
                val rectCorner = 6.dp
                val animatedCorner = lerp(circleCorner, rectCorner, progress)

                val animatedColor by transition.animateColor(
                    initialValue = Color(0xFF2196F3),
                    targetValue = Color(0xFFE91E63),
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 2400
                            Color(0xFF2196F3) at 0
                            Color(0xFFE91E63) at 450
                            Color(0xFF4CAF50) at 900
                            Color(0xFFFFC107) at 1350
                            Color(0xFF2196F3) at 1800
                        }
                    ),
                    label = "color"
                )

                Box(
                    modifier = Modifier
                        .size(size)
                        .graphicsLayer {
                            translationY = (travel.toPx() * offsetProgress)
                            rotationZ = rotation
                        }
                        .clip(RoundedCornerShape(animatedCorner))
                        .background(animatedColor)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Processing…",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = BoldFont,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Please wait while we complete your request",
                fontSize = 14.sp,
                fontFamily = RegularFont,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}