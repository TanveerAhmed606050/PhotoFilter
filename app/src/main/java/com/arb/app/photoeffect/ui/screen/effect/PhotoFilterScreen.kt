package com.arb.app.photoeffect.ui.screen.effect

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.network.isNetworkAvailable
import com.arb.app.photoeffect.repository.compressImage
import com.arb.app.photoeffect.ui.commonViews.InfoMsgDialog
import com.arb.app.photoeffect.ui.commonViews.LoadingOverlay
import com.arb.app.photoeffect.ui.screen.effect.models.EnhanceColorizeDto
import com.arb.app.photoeffect.ui.screen.effect.models.IngredientDto
import com.arb.app.photoeffect.ui.screen.effect.models.ScratchDto
import com.arb.app.photoeffect.ui.screen.effect.models.SolidDto
import com.arb.app.photoeffect.ui.screen.plus.PhotoVM
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.util.Utils.createMultipartBodyPart
import com.arb.app.photoeffect.util.Utils.uriToFile
import java.net.URLDecoder
import kotlin.math.roundToInt

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun PhotoFilterScreen(
    navController: NavController, photoVM: PhotoVM, imageUri: String,
    effect: String
) {
    val context = LocalContext.current
    val imageBitmap = photoVM.bitmap
    val isLoading = photoVM.isLoading
    val error = photoVM.error
    val decodedUri = remember { Uri.parse(URLDecoder.decode(imageUri, "utf-8")) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val connectivityError = stringResource(id = R.string.network_error)
    if (error.isNotEmpty()) {
        showInfoDialog = true
        photoVM.error = ""
    }
    if (showInfoDialog) {
        InfoMsgDialog(status = false,
            msg = connectivityError,
            cancelBtnClick = {
                showInfoDialog = false
            })
    }
    if (isNetworkAvailable(context)) {
        LaunchedEffect(Unit) {
            val file = uriToFile(context, uri = decodedUri!!)
            val smallFile = compressImage(file)
            Log.d(
                "ImageSize",
                "Original size: ${file.length()} bytes (${file.length() / 1024} KB)\n" +
                        "Small size: ${smallFile.length()} bytes (${smallFile.length() / 1024} KB)"
            )
            val imagePart = createMultipartBodyPart(smallFile, "file")
            when (effect) {
                "Solid Bg" -> photoVM.solidBgApi(SolidDto("44,110,73", imagePart))
                "Gradient Bg" ->
                    photoVM.gradientBgApi(
                        IngredientDto(
                            color1 = "44,110,73",
                            color2 = "255,255,255", file = imagePart
                        )
                    )

                "Remove Scratch" -> photoVM.removeScratchApi(
                    ScratchDto(
                        imagePart,
                        "-1",
                        false,
                        false
                    )
                )

                else -> {
                    photoVM.filterApi(EnhanceColorizeDto(imagePart), effect)
                }
            }
        }
    } else {
        showInfoDialog = true
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
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay either imageBitmap or placeholder on the left part
//            val leftImagePainter = if (imageBitmap != null) {
//                BitmapPainter(imageBitmap.asImageBitmap())
//            } else {
//                painterResource(id = R.drawable.potrait_cutout)
//            }
            imageBitmap?.asImageBitmap()?.let {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Left Overlay",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            clip = true
                            shape = RectangleShape
                        }
                        .drawWithContent {
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
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            LoadingOverlay(isLoading)
        }

    }
}

@Preview
@Composable
fun ChoosePhotoPreview() {
    PhotoEffectTheme {
        PhotoFilterUI(isLoading = false, Uri.parse(""), imageBitmap = null)
    }
}