package com.arb.app.photoeffect.ui.screen.history

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.ui.commonViews.WhiteAppButton
import com.arb.app.photoeffect.ui.screen.history.models.GalleryImage
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.DarkBlue
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    var images by remember { mutableStateOf<List<GalleryImage>>(emptyList()) }
    var selectedImages by remember { mutableStateOf<List<GalleryImage>>(emptyList()) }
    var isSelectionMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        images = getImagesFromGallery(context)
        Log.d("lsdjg", "HistoryScreen: ${images.size}")
    }

    HistoryScreenUI(
        images = images,
        selectedImages = selectedImages,
        isSelectionMode = isSelectionMode,
        onSelectClick = { action ->
            when (action) {
                "select" -> isSelectionMode = true
                "cancel" -> {
                    isSelectionMode = false
                    selectedImages = emptyList()
                }

                "select_all" -> {
                    selectedImages = images
                }
            }
        },
        onImageClick = { img ->
            if (isSelectionMode) {
                selectedImages =
                    if (selectedImages.contains(img)) {
                        selectedImages - img
                    } else {
                        selectedImages + img
                    }
            }
        },
        onDelete = {
            selectedImages.forEach { galleryImage ->
                context.contentResolver.delete(galleryImage.uri, null, null)
            }
            images = getImagesFromGallery(context) // refresh list
            selectedImages = emptyList()
            isSelectionMode = false
        }
    )
}

@Composable
fun HistoryScreenUI(
    images: List<GalleryImage>,
    selectedImages: List<GalleryImage>,
    isSelectionMode: Boolean,
    onSelectClick: (String) -> Unit,
    onImageClick: (GalleryImage) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00475C),
                            Color(0xFF007E9F)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            Text(
                text = "History",
                fontFamily = BoldFont,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(30.dp))

            if (images.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!isSelectionMode) {
                        HistoryOption(
                            text = stringResource(id = R.string.select),
                            onSelectClick = { onSelectClick("select") }
                        )
                    } else {
                        HistoryOption(
                            text = stringResource(id = R.string.select_all),
                            onSelectClick = { onSelectClick("select_all") }
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        HistoryOption(
                            text = stringResource(id = R.string.cancel),
                            onSelectClick = { onSelectClick("cancel") }
                        )
                    }
                }
            }

            if (images.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(images.size) { index ->
                        val img = images[index]
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clickable { onImageClick(img) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = img.uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )

                            if (isSelectionMode) {
                                Icon(
                                    painter = if (selectedImages.contains(img))
                                        painterResource(id = R.drawable.radio_button_checked)
                                    else
                                        painterResource(id = R.drawable.radio_button_unchecked),
                                    contentDescription = null,
                                    tint = if (selectedImages.contains(img)) DarkBlue else Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (images.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No History",
                    color = Color.White,
                    fontFamily = BoldFont,
                    fontSize = 16.sp,
                )
            }
        }
        if (selectedImages.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 50.dp)
            ) {
                WhiteAppButton(
                    buttonText = stringResource(id = R.string.delete),
                    onButtonClick = { onDelete() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(150.dp))
            }
        }

    }
}

@Composable
fun HistoryOption(
    text: String,
    onSelectClick: (String) -> Unit
) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = RegularFont,
        fontSize = 16.sp,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .clickable { onSelectClick(text) }
    )
}

fun getImagesFromGallery(context: Context): List<GalleryImage> {
    val images = mutableListOf<GalleryImage>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.RELATIVE_PATH
    )

    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf(Environment.DIRECTORY_PICTURES + "/Photofia%")
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            images.add(GalleryImage(contentUri))
        }
    }
    return images
}

@Preview
@Composable
fun HistoryScreenPreview() {
    PhotoEffectTheme {
        HistoryScreenUI(images = listOf(), selectedImages = listOf(), onSelectClick = {},
            onDelete = {}, isSelectionMode = false, onImageClick = {}
        )
    }
}