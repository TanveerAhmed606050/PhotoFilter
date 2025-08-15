package com.arb.app.photoeffect.ui.screen.home

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

/**
 * Simple, production-ready(ish) Collage Maker in Jetpack Compose
 * - Pick multiple images
 * - Choose a layout template
 * - Pan / Zoom / Rotate each slot
 * - Export a high-res PNG to gallery (Pictures/Collages)
 *
 * Drop this file into your project and call CollageMakerScreen() from your UI.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollageMakerScreen() {
    val context = LocalContext.current

    var template by remember { mutableStateOf(CollageTemplate.Grid2x2) }
    var slots by remember { mutableStateOf(template.initialSlots()) }

    // Re-create slots when template changes
    LaunchedEffect(template) { slots = template.initialSlots() }

    val pickImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNullOrEmpty()) return@rememberLauncherForActivityResult
        // Fill slot URIs in order; extra URIs ignored; fewer URIs leave some slots empty
        slots = slots.mapIndexed { index, slot ->
            if (index < uris.size) slot.copy(uri = uris[index]) else slot
        }
    }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Collage Maker") })
        },
        bottomBar = {
            BottomAppBar(actions = {
                TextButton(onClick = { pickImages.launch("image/*") }) {
                    Text("Add Photos")
                }
                Spacer(Modifier.width(8.dp))
                var saving by remember { mutableStateOf(false) }
                Button(
                    onClick = {
//                        if (!saving) {
//                            saving = true
//                            // Launch save in a coroutine scope tied to composition
//                            // (In real app, hoist to ViewModel)
//                            LaunchedEffect(Unit) {
//                                val result = saveCollageToGallery(
//                                    context = context,
//                                    template = template,
//                                    slots = slots,
//                                    // Export at 2000px on the shortest side
//                                    exportMinSizePx = 2000
//                                )
//                                saving = false
//                                val msg = result.getOrElse { it.message ?: "Failed" }
//                                val ok = result.isSuccess
//                                val snackBarHostState = SnackbarHostState()
//                                // local snackbar host not accessible easily; use Android Toast
//                                android.widget.Toast.makeText(
//                                    context,
//                                    if (ok) "Saved to Gallery: $msg" else "Error: $msg",
//                                    android.widget.Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
                    }, enabled = slots.any { it.uri != null }
                ) { Text("Save Collage") }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Templates row
            LazyRow(
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CollageTemplate.entries) { item ->
                    FilterChip(
                        selected = item == template,
                        onClick = { template = item },
                        label = { Text(item.label) }
                    )
                }
            }

            // Collage canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF111214))
                    .onGloballyPositioned { canvasSize = it.size },
                contentAlignment = Alignment.Center
            ) {
                CollageCanvas(
                    modifier = Modifier.fillMaxSize(),
                    template = template,
                    slots = slots,
                    onUpdateSlot = { idx, new ->
                        slots = slots.toMutableList().also { it[idx] = new }
                    }
                )
            }

            // Helper text
            Text(
                "Tip: pinch to zoom/rotate, drag to pan. Double-tap to reset a photo.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// --- Collage data & templates -------------------------------------------------

data class CollageSlot(
    val frame: RectF01,                 // slot rectangle in [0..1] coords within the collage
    val uri: Uri? = null,               // image content URI
    val scale: Float = 1f,              // relative scale applied to the image
    val rotation: Float = 0f,           // in degrees
    val offsetXFrac: Float = 0f,        // pan X in fractions of frame width
    val offsetYFrac: Float = 0f         // pan Y in fractions of frame height
)

/** Simple Rect with 0..1 coordinates (fractions of the full canvas) */
data class RectF01(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    fun width() = right - left
    fun height() = bottom - top
}

enum class CollageTemplate(val label: String, val frames: List<RectF01>) {
    Grid2x2(
        label = "2x2",
        frames = listOf(
            RectF01(0f, 0f, 0.5f, 0.5f), RectF01(0.5f, 0f, 1f, 0.5f),
            RectF01(0f, 0.5f, 0.5f, 1f), RectF01(0.5f, 0.5f, 1f, 1f)
        )
    ),
    Tall3(
        label = "1 + 2",
        frames = listOf(
            RectF01(0f, 0f, 1f, 0.6f), // big top
            RectF01(0f, 0.6f, 0.5f, 1f),
            RectF01(0.5f, 0.6f, 1f, 1f)
        )
    ),
    Left1Right2(
        label = "2 + 1",
        frames = listOf(
            RectF01(0f, 0f, 0.6f, 1f),
            RectF01(0.6f, 0f, 1f, 0.5f),
            RectF01(0.6f, 0.5f, 1f, 1f)
        )
    ),
    Grid3x3(
        label = "3x3",
        frames = buildList {
            val step = 1f / 3f
            for (r in 0..2) for (c in 0..2) add(
                RectF01(c * step, r * step, (c + 1) * step, (r + 1) * step)
            )
        }
    );

    fun initialSlots(): List<CollageSlot> = frames.map { CollageSlot(frame = it) }

    companion object
}

// --- Collage canvas composable ------------------------------------------------

@Composable
private fun CollageCanvas(
    modifier: Modifier = Modifier,
    template: CollageTemplate,
    slots: List<CollageSlot>,
    onUpdateSlot: (index: Int, updated: CollageSlot) -> Unit
) {
    BoxWithConstraints(modifier) {
        val W = constraints.maxWidth
        val H = constraints.maxHeight
        val density = LocalDensity.current

        // Draw each frame as a clipped box
        template.frames.forEachIndexed { index, rect ->
            val x = rect.left * W
            val y = rect.top * H
            val w = rect.width() * W
            val h = rect.height() * H
            val slot = slots[index]

            Box(
                modifier = Modifier
                    .offset { IntOffset(x.toInt(), y.toInt()) }
                    .size(with(density) { w.toDp() }, with(density) { h.toDp() })
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF222326))
            ) {
                val state = remember(slot.uri) {
                    mutableStateOf(slot)
                }

                // Keep outer state in sync when we mutate locally
                DisposableEffect(state.value) {
                    onDispose { }
                }

                val transformState = remember {
                    TransformableState { zoomChange, panChange, rotationChange ->
                        val fw = w.toFloat()
                        val fh = h.toFloat()
                        val newScale = (state.value.scale * zoomChange).coerceIn(0.3f, 8f)
                        val newRot = (state.value.rotation + rotationChange)
                        val newX = state.value.offsetXFrac + (panChange.x / fw)
                        val newY = state.value.offsetYFrac + (panChange.y / fh)
                        state.value = state.value.copy(
                            scale = newScale,
                            rotation = newRot,
                            offsetXFrac = newX.coerceIn(-2f, 2f),
                            offsetYFrac = newY.coerceIn(-2f, 2f)
                        )
                        onUpdateSlot(index, state.value)
                    }
                }

                // Photo content
                if (slot.uri != null) {
                    Box(
                        Modifier.fillMaxSize()
                            .transformable(transformState)
                            .pointerInput(slot.uri) {
                                detectTapGestures(onDoubleTap = {
                                    state.value = state.value.copy(scale = 1f, rotation = 0f, offsetXFrac = 0f, offsetYFrac = 0f)
                                    onUpdateSlot(index, state.value)
                                })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Translate/scale/rotate around center
                        val translateX = state.value.offsetXFrac * w
                        val translateY = state.value.offsetYFrac * h
                        val rotation = state.value.rotation
                        val scale = state.value.scale

                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = translateX
                                    translationY = translateY
                                    rotationZ = rotation
                                    scaleX = scale
                                    scaleY = scale
                                    clip = false
                                }
                        ) {
                            AsyncImage(
                                model = slot.uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    // Empty placeholder
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tap Add Photos", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

// --- Saving / Export ----------------------------------------------------------

suspend fun saveCollageToGallery(
    context: Context,
    template: CollageTemplate,
    slots: List<CollageSlot>,
    exportMinSizePx: Int = 2000,
    fileName: String = "collage_${System.currentTimeMillis()}"
): Result<String> = withContext(Dispatchers.IO) {
    try {
        // Determine export size: keep aspect from template's full area (1:1).
        // You can customize. Here we choose square canvas.
        val width = exportMinSizePx
        val height = exportMinSizePx

        val outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(outBitmap)
        canvas.drawColor(android.graphics.Color.parseColor("#111214"))

        slots.forEach { slot ->
            val uri = slot.uri ?: return@forEach
            val bmp = context.contentResolver.openInputStream(uri)?.use { input ->
                android.graphics.BitmapFactory.decodeStream(input)
            } ?: return@forEach

            val frame = slot.frame
            val frameLeft = (frame.left * width)
            val frameTop = (frame.top * height)
            val frameW = (frame.width() * width)
            val frameH = (frame.height() * height)

            // Compute base scale to cover the frame (like ContentScale.Crop)
            val scaleToCover = max(frameW / bmp.width.toFloat(), frameH / bmp.height.toFloat())

            val matrix = Matrix()
            matrix.postTranslate(-bmp.width / 2f, -bmp.height / 2f) // center at origin
            matrix.postScale(scaleToCover * slot.scale, scaleToCover * slot.scale)
            matrix.postRotate(slot.rotation)
            matrix.postTranslate(
                frameLeft + frameW / 2f + slot.offsetXFrac * frameW,
                frameTop + frameH / 2f + slot.offsetYFrac * frameH
            )

            // Clip to frame and draw
            val saveCount = canvas.save()
            canvas.clipRect(frameLeft, frameTop, frameLeft + frameW, frameTop + frameH)
            canvas.drawBitmap(bmp, matrix, null)
            canvas.restoreToCount(saveCount)

            bmp.recycle()
        }

        // Insert into MediaStore
        val mimeType = "image/png"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Collages")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return@withContext Result.failure(IllegalStateException("Cannot create MediaStore entry"))
        context.contentResolver.openOutputStream(uri)?.use { out ->
            outBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } ?: return@withContext Result.failure(IllegalStateException("Cannot open output stream"))

        outBitmap.recycle()
        Result.success("${if (Build.VERSION.SDK_INT >= 29) "Pictures/Collages/" else "Gallery/"}$fileName.png")
    } catch (t: Throwable) {
        Result.failure(t)
    }
}