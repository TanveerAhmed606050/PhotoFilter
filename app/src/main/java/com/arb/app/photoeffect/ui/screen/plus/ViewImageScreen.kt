package com.arb.app.photoeffect.ui.screen.plus

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.screen.plus.models.ImageFilter
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.ui.theme.RegularFont
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun ViewImageScreen(navController: NavController, imageUri: String) {
    val decodedUri = remember { Uri.parse(URLDecoder.decode(imageUri, "utf-8")) }
    ViewImageUI(imageUrl = decodedUri,
        onBackIc = { navController.popBackStack() },
        onFilterClick = { effect ->
            navController.navigate(
                Screen.PhotoFilterScreen.route + "imageUrl/${
                    URLEncoder.encode(
                        imageUri,
                        "utf-8"
                    )
                }/$effect"
            )
        })
}

@Composable
fun ViewImageUI(
    imageUrl: Uri?,
    onFilterClick: (String) -> Unit,
    onBackIc: () -> Unit
) {
    val filterList = listOf(
        ImageFilter(filterIc = R.drawable.magic_wand, "Enhance Photo"),
        ImageFilter(filterIc = R.drawable.remove_ads, "Remove Scratch"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Remove Scratch"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Colorize"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Cartoonize"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Edit"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Portrait Cutout"),
        ImageFilter(filterIc = R.drawable.magic_wand, "Filter"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.back_ic), contentDescription = "",
            modifier = Modifier
                .padding(start = 20.dp)
                .clickable { onBackIc() }
                .size(24.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.height(12.dp))
// Zoomable Image
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ZoomableAsyncImage(imageUrl)
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filterList.size) { index ->
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = filterList[index].filterIc),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = filterList[index].filterName,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable { onFilterClick(filterList[index].filterName) },
                        textAlign = TextAlign.Center,
                        fontFamily = RegularFont
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableAsyncImage(imageUrl: Uri?) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val scaleAnim = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        val target = if (scale > 1f) 1f else 2f
                        scale = target
                        offset = Offset.Zero
                        coroutineScope.launch {
                            scaleAnim.animateTo(
                                targetValue = target,
                                animationSpec = tween(250)
                            )
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    // Wait for first touch
                    awaitFirstDown()

                    // Track transforms while fingers are on screen
                    do {
                        val event = awaitPointerEvent()
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()

                        val newScale = (scale * zoomChange).coerceIn(0.5f, 5f)
                        scale = newScale
                        coroutineScope.launch {
                            scaleAnim.snapTo(newScale)
                        }

                        if (scale > 1f) {
                            offset += panChange
                        } else {
                            offset = Offset.Zero
                        }
                    } while (event.changes.any { it.pressed })

                    // 👆 Fingers released → reset if below 1x
                    if (scale < 1f) {
                        scale = 1f
                        offset = Offset.Zero
                        coroutineScope.launch {
                            scaleAnim.animateTo(1f, tween(250))
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Zoomable Image",
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.enhance_photo_2),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scaleAnim.value,
                    scaleY = scaleAnim.value,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}

@Preview
@Composable
fun ViewImageScreenPreview() {
    PhotoEffectTheme {
        ViewImageUI(imageUrl = null,
            onFilterClick = {},
            onBackIc = {})
    }
}