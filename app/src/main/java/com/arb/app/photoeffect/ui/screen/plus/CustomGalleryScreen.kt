package com.arb.app.photoeffect.ui.screen.plus

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.arb.app.photoeffect.navigation.Screen
import java.io.File

@Composable
fun CustomGalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri>() }
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraPermission = Manifest.permission.CAMERA

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imageUris.clear()
                imageUris.addAll(getAllImages(context))
            }
        }
    )
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
            }
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            cameraImageUri.value = uri
            if (uri != null) {
                takePictureLauncher.launch(uri)
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                context,
                storagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imageUris.clear()
            imageUris.addAll(getAllImages(context))
        } else {
            permissionLauncher.launch(storagePermission)
        }
    }

    CustomGalleryContent(imageUris = imageUris, onImageSelected = {
        navController.navigate(Screen.ChoosePhotoScreen.route)
    },
        onBackBtn = { navController.popBackStack() },
        onCameraClick = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    cameraPermission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val uri = createImageUri(context)
                    cameraImageUri.value = uri
                    if (uri != null) {
                        takePictureLauncher.launch(uri)
                    }
                }

                else -> {
                    cameraPermissionLauncher.launch(cameraPermission)
                }
            }
        })
}

@Composable
fun CustomGalleryContent(
    imageUris: List<Uri>,
    onImageSelected: (Uri) -> Unit,
    onBackBtn: () -> Unit,
    onCameraClick: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(30.dp))
        GalleryTopBar(
            onBack = { onBackBtn() },
            onCameraClick = { onCameraClick() },
            folderName = "Gallery"
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clickable { onImageSelected(uri) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


fun getAllImages(context: Context, limit: Int = 500): List<Uri> {
    val imageList = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val cursor = context.contentResolver.query(
        queryUri,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        var count = 0
        while (cursor.moveToNext() && count < limit) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(queryUri, id)
            imageList.add(contentUri)
            count++
        }
    }

    return imageList
}


fun createImageUri(context: Context): Uri? {
    val imageFile = File.createTempFile(
        "captured_", ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Composable
fun GalleryTopBar(onBack: () -> Unit, onCameraClick: () -> Unit, folderName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = folderName, fontWeight = FontWeight.Bold)
//            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }

        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_camera),
            contentDescription = "Camera",
            modifier = Modifier.clickable { onCameraClick() }
        )
    }
}
