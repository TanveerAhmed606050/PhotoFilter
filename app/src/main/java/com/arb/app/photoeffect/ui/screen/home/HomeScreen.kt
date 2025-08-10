package com.arb.app.photoeffect.ui.screen.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.commonViews.Header
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun HomeScreen(navController: NavController) {
    HomeScreenUI(onPhotoClick = { effect ->
        navController.navigate(Screen.CustomGalleryScreen.route + "?data/$effect")
    })
}

@Composable
fun HomeScreenUI(
    onPhotoClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00475C), // Dark top
                        Color(0xFF007E9F)  // Lighter bottom
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        Header()
        Spacer(modifier = Modifier.height(16.dp))
        CardWithImageAndText(
            imageRes = R.drawable.enhance_photo,
            title = "Enhance Photo",
            buttonText = stringResource(id = R.string.go),
            onPhotoClick = { onPhotoClick(it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SmallFeatureCard(
                "Remove Scratch",
                R.drawable.remove_scratch,
                modifier = Modifier.weight(1f),
                showArrow = true,
                onPhotoClick = { onPhotoClick(it) }
            )
            SmallFeatureCard(
                "Colorize",
                R.drawable.colourize,
                showArrow = true,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )

        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoCard(
                title = "Cartoonize",
                imageUrl = R.drawable.cartonize,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )
            PhotoCard(
                title = "Solid Bg",
                imageUrl = R.drawable.style_transfer,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )
            PhotoCard(
                title = "Gradient Bg",
                imageUrl = R.drawable.edit,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SmallFeatureCard(
                "Portrait Cutout",
                R.drawable.potrait_cutout,
                showArrow = true,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )
            SmallFeatureCard(
                "Art Filter",
                R.drawable.art_filter,
                showArrow = true,
                modifier = Modifier.weight(1f),
                onPhotoClick = { onPhotoClick(it) }
            )
        }

    }
}

@Composable
fun PhotoCard(
    title: String,
    imageUrl: Int,
    onPhotoClick: (String) -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .height(200.dp)
            .clickable { onPhotoClick(title) }
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            error = painterResource(id = imageUrl),
            placeholder = painterResource(id = imageUrl)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun CardWithImageAndText(
    imageRes: Int,
    title: String,
    buttonText: String,
    onPhotoClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable { onPhotoClick(title) }
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                title, color = Color.White, fontSize = 12.sp,
                fontFamily = RegularFont
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.Gray.copy(0.4f), RoundedCornerShape(20.dp))
                .padding(vertical = 8.dp, horizontal = 24.dp)
        ) {
            Text(buttonText, color = Color.White)
        }
    }
}

@Composable
fun SmallFeatureCard(
    title: String, imageRes: Int, modifier: Modifier = Modifier,
    showArrow: Boolean = false,
    onPhotoClick: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onPhotoClick(title) }
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                fontFamily = RegularFont,
                modifier = Modifier.weight(1f),
            )
            if (showArrow) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.4f))
                        .clickable { /* Handle click */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.next_ic),
                        contentDescription = "Go",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    PhotoEffectTheme {
        HomeScreenUI(onPhotoClick = {})
    }
}