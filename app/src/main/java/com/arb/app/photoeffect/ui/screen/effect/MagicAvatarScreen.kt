package com.arb.app.photoeffect.ui.screen.effect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.commonViews.WhiteAppButton
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme

@Composable
fun MagicAvatarScreen(navController: NavController) {
    MagicAvatarsScreen(createBtnClick = {
        navController.navigate(Screen.ChooseEffectScreen.route)
    })
}

@Composable
fun MagicAvatarsScreen(createBtnClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Magic Avatars",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Generate amazing photos from your selfies with\none of the most advanced AI ever created.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Star Wand + Orbit + Overlapping Avatars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                MagicWandAndOrbit()
                OverlappingAvatars()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom AI Avatar
            Image(
                painter = painterResource(id = R.drawable.enhance_photo),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.weight(1f))

            WhiteAppButton(buttonText = stringResource(id = R.string.create_now),
                onButtonClick = { createBtnClick() })
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MagicWandAndOrbit() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw circular orbits
        val center = Offset(size.width * 0.2f, size.height * 0.4f)
        val radii = listOf(80f, 120f, 160f)
        radii.forEach { radius ->
            drawCircle(
                color = Color.Gray.copy(alpha = 0.4f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Stars on orbits
        val starOffset = listOf(
            Offset(center.x + 80f, center.y),
            Offset(center.x + 120f, center.y - 40f),
            Offset(center.x + 160f, center.y + 20f)
        )
        starOffset.forEach {
            drawCircle(Color.White, radius = 4.dp.toPx(), center = it)
        }

        // Wand icon (simple line + star)
        drawLine(
            color = Color.White,
            start = Offset(center.x - 20f, center.y + 60f),
            end = Offset(center.x + 20f, center.y + 100f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color.White,
            center = Offset(center.x - 28f, center.y + 52f),
            radius = 8f
        )
    }
}

@Composable
fun OverlappingAvatars() {
    val images = listOf(
        R.drawable.style_transfer,
        R.drawable.edit,
        R.drawable.cartonize,
        R.drawable.enhance_photo
    )
    val positions = listOf(
        Pair(180.dp, 0.dp),
        Pair(230.dp, 20.dp),
        Pair(200.dp, 70.dp),
        Pair(250.dp, 100.dp)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        images.forEachIndexed { index, imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .offset(x = positions[index].first, y = positions[index].second)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
fun MagicAvatarPreview() {
    PhotoEffectTheme {
        MagicAvatarsScreen(createBtnClick = {})
    }
}