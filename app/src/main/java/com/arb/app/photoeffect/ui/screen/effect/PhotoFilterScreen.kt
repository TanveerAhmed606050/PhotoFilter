package com.arb.app.photoeffect.ui.screen.effect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.ui.commonViews.LoadingOverlay
import com.arb.app.photoeffect.ui.screen.plus.PhotoVM
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import java.net.URLDecoder
import kotlin.math.roundToInt

@Composable
fun PhotoFilterScreen(
    navController: NavController, photoVM: PhotoVM, imageUri: String,
    effect: String
) {
    val context = LocalContext.current
    val imageBitmap = photoVM.bitmap
    val isLoading = photoVM.isLoading
    val decodedUri = remember { Uri.parse(URLDecoder.decode(imageUri, "utf-8")) }
    LaunchedEffect(Unit) {
        photoVM.upload(context = context, decodedUri, effect)
    }
    PhotoFilterUI(isLoading = isLoading, decodedUri, imageBitmap)
}

@Composable
fun PhotoFilterUI(
    isLoading: Boolean,
    imageUrl: Uri?, imageBitmap: Bitmap?
) {
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
                                offsetX =
                                    (offsetX + dragAmount.x).coerceIn(0f, size.width.toFloat())
                            }
                        }
                    )
                }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Right Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Colorized",
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
            } ?: Text("")

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
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(color = Color.Black)
//                .align(Alignment.BottomCenter),
//            contentAlignment = Alignment.Center
//        ) {
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(6.dp)
//            ) {
//                items(filterList.size) { index ->
//                    Text(
//                        text = filterList[index], fontSize = 14.sp, color = Color.Black,
//                        modifier = Modifier
//                            .background(Color.White, shape = RoundedCornerShape(12.dp))
//                            .padding(horizontal = 12.dp),
//                        textAlign = TextAlign.Center,
//                        fontFamily = BoldFont
//                    )
//                }
//            }
//        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            LoadingOverlay(isLoading)
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
        PhotoFilterUI(isLoading = false, Uri.parse(""), imageBitmap = null)
    }
}