package com.arb.app.photoeffect.ui.screen.plus

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import kotlin.math.roundToInt

@Composable
fun ChoosePhotoScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    ChoosePhotoUI(selectedImageUri)
}

@Composable
fun ChoosePhotoUI(imageUrl: Uri?) {
    val filterList = listOf(
        "Enhance Photo",
        "Remove Scratch",
        "Colorize",
        "Cartoonize",
        "Style Transfer",
        "Edit",
        "Portrait Cutout",
        "Art Filter"
    )
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidth.toPx() }

    var offsetX by remember { mutableFloatStateOf(screenWidthPx / 2f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { touchPoint ->
                            if (touchPoint.x in (offsetX - 40)..(offsetX + 40)) {
                                isDragging = true
                            }
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { _, dragAmount ->
                            if (isDragging) {
                                offsetX = (offsetX + dragAmount.x).coerceIn(0f, size.width.toFloat())
                            }
                        }
                    )
                }
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?ixid=MnwxMjA3fDB8MXxzZWFyY2h8Mnx8Zm9yZXN0fGVufDB8fHx8MTY4NzI3NzgwOA&auto=format&fit=crop&w=1080&q=80",
                contentDescription = "Right Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            AsyncImage(
                model = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?ixid=MnwxMjA3fDB8MXxzZWFyY2h8M3x8bW91bnRhaW5zfGVufDB8fHx8MTY4NzI3NzgwOA&auto=format&fit=crop&w=1080&q=80",
                contentDescription = "Left Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = true
                        shape = RectangleShape
                    }
                    .drawWithContent {
                        // Clip manually to offsetX
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = offsetX,
                            bottom = size.height
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .offset { IntOffset((offsetX - 20).roundToInt(), 0) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .align(Alignment.Center)
                        .background(Color.White)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Black)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(filterList.size) { index ->
                    Text(
                        text = filterList[index], fontSize = 14.sp, color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun openGallery(
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    permissionLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context, Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        galleryLauncher.launch("image/*")
    } else {
        // Request a permission
        permissionLauncher.launch("image/*")
    }
}

@Preview
@Composable
fun ChoosePhotoPreview() {
    PhotoEffectTheme {
        ChoosePhotoUI(Uri.parse(""))
    }
}