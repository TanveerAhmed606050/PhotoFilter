package com.arb.app.photoeffect.ui.screen.effect

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.ColorFilter
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
import com.arb.app.photoeffect.ui.commonViews.MorphingDropSpinnerOverlay
import com.arb.app.photoeffect.ui.screen.effect.models.EnhanceColorizeDto
import com.arb.app.photoeffect.ui.screen.effect.models.IngredientDto
import com.arb.app.photoeffect.ui.screen.effect.models.ScratchDto
import com.arb.app.photoeffect.ui.screen.effect.models.SolidDto
import com.arb.app.photoeffect.ui.screen.plus.PhotoVM
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.util.Utils.createMultipartBodyPart
import com.arb.app.photoeffect.util.Utils.uriToFile
import java.io.IOException
import java.io.OutputStream
import java.net.URLDecoder
import kotlin.math.roundToInt

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun PhotoFilterScreen(
    navController: NavController, photoVM: PhotoVM, imageUri: String, effect: String
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
        InfoMsgDialog(status = false, msg = connectivityError, cancelBtnClick = {
            showInfoDialog = false
        })
    }
    if (isNetworkAvailable(context)) {
        LaunchedEffect(Unit) {
            val file = uriToFile(context, uri = decodedUri!!)
            val smallFile = compressImage(file)
            Log.d(
                "ImageSize",
                "Original size: ${file.length()} bytes (${file.length() / 1024} KB)\n" + "Small size: ${smallFile.length()} bytes (${smallFile.length() / 1024} KB)"
            )
            val imagePart = createMultipartBodyPart(smallFile, "file")
            when (effect) {
                "Solid Bg" -> photoVM.solidBgApi(SolidDto("255,255,255", imagePart))
                "Gradient Bg" -> photoVM.gradientBgApi(
                    IngredientDto(
                        color1 = "255,126,95", color2 = "254,180,123", file = imagePart
                    )
                )

                "Remove Scratch" -> photoVM.removeScratchApi(
                    ScratchDto(
                        imagePart, "-1", true, true
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
    PhotoFilterUI(isLoading = isLoading,
        decodedUri,
        imageBitmap,
        onBackIconClick = { navController.popBackStack() },
        onSaveIconClick = {
            if (imageBitmap != null) {
                saveBitmapToGallery(context = context, imageBitmap,
                    saveMessage = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                        navController.popBackStack()
                    })
            }
        })
}

@Composable
fun PhotoFilterUI(
    isLoading: Boolean,
    imageUrl: Uri?,
    imageBitmap: Bitmap?,
    onBackIconClick: () -> Unit,
    onSaveIconClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidth.toPx() }

    var offsetX by remember { mutableFloatStateOf(screenWidthPx / 2f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = { touchPoint ->
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
                    })
            }) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Right Image",
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            imageBitmap?.asImageBitmap()?.let {
                Image(bitmap = imageBitmap.asImageBitmap(),
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
                                left = 0f, top = 0f, right = offsetX, bottom = size.height
                            ) {
                                this@drawWithContent.drawContent()
                            }
                        })
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .offset { IntOffset((offsetX - 20).roundToInt(), 0) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .align(Alignment.Center)
                            .background(Color.White)
                    )
                }
            }
            FilterHeader(
                onSaveIconClick = { onSaveIconClick() },
                onBackIconClick = { onBackIconClick() })
        }
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            MorphingDropSpinnerOverlay(visible = isLoading) // covers whole screen
        }

    }
}

@Composable
fun FilterHeader(
    onBackIconClick: () -> Unit, onSaveIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 50.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_ic),
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackIconClick() },
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.save_ic),
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .clickable { onSaveIconClick() },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    fileName: String = "image_${System.currentTimeMillis()}.jpg",
    saveMessage: (String) -> Unit
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Photofia")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        var stream: OutputStream? = null
        try {
            stream = resolver.openOutputStream(it)
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                saveMessage("Image saved in gallery")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            saveMessage("${e.printStackTrace()}")
        } finally {
            stream?.close()
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }
}

@Preview
@Composable
fun ChoosePhotoPreview() {
    PhotoEffectTheme {
        PhotoFilterUI(isLoading = false,
            Uri.parse(""),
            imageBitmap = null,
            onBackIconClick = {},
            onSaveIconClick = {})
    }
}