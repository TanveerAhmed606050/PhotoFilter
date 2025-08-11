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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.commonViews.LoadingOverlay
import com.arb.app.photoeffect.ui.screen.plus.models.ImageFolder
import java.io.File
import java.net.URLEncoder

@Composable
fun CustomGalleryScreen(navController: NavController, effectType: String?) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val folders = remember { mutableStateListOf<ImageFolder>() }
    var selectedFolder by remember { mutableStateOf<ImageFolder?>(null) }
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraPermission = Manifest.permission.CAMERA

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                folders.clear()
                folders.addAll(getImageFolders(context))
            }
        }
    )
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri -> /* handle captured image */ }
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
            isLoading = true
            folders.clear()
            folders.addAll(getImageFolders(context))
            isLoading = false
        } else {
            permissionLauncher.launch(storagePermission)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedFolder == null) {
            FolderListScreen(
                folders = folders,
                onFolderClick = { folder -> selectedFolder = folder },
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
                }
            )
        } else {
            FolderImagesScreen(
                folder = selectedFolder!!,
                onImageSelected = { imageUri ->
                    val imageUriString = imageUri.toString()
                    navController.popBackStack()
                    if (!effectType.isNullOrEmpty()) {
                        navController.navigate(
                            Screen.PhotoFilterScreen.route + "imageUrl/${
                                URLEncoder.encode(imageUriString, "utf-8")
                            }/$effectType"
                        )
                    } else {
                        navController.navigate(
                            Screen.ViewImageScreen.route + "data/${
                                URLEncoder.encode(imageUriString, "utf-8")
                            }"
                        )
                    }
                },
                onBackBtn = { selectedFolder = null },
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
                }
            )
        }
        if (isLoading) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                LoadingOverlay(isLoading)
            }
        }
    }

}


@Composable
fun FolderImagesScreen(
    folder: ImageFolder,
    onImageSelected: (Uri) -> Unit,
    onBackBtn: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(30.dp))
        GalleryTopBar(
            onBack = { onBackBtn() },
            onCameraClick = { onCameraClick() },
            folderName = folder.folderName
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(folder.imageUris) { uri ->
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

@Composable
fun FolderListScreen(
    folders: List<ImageFolder>,
    onFolderClick: (ImageFolder) -> Unit,
    onBackBtn: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(30.dp))
        GalleryTopBar(
            onBack = { onBackBtn() },
            onCameraClick = { onCameraClick() },
            folderName = "Albums"
        )

        LazyColumn {
            items(folders.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFolderClick(folders[index]) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(folders[index].imageUris.firstOrNull()),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(folders[index].folderName, fontWeight = FontWeight.Bold)
                        Text(
                            "${folders[index].imageUris.size} photos",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

fun getImageFolders(context: Context, limitPerFolder: Int = 500): List<ImageFolder> {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val folderMap = mutableMapOf<String, MutableList<Uri>>()

    context.contentResolver.query(
        queryUri,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val folderName = cursor.getString(bucketColumn) ?: "Unknown"
            val contentUri = ContentUris.withAppendedId(queryUri, id)

            val list = folderMap.getOrPut(folderName) { mutableListOf() }
            if (list.size < limitPerFolder) {
                list.add(contentUri)
            }
        }
    }

    return folderMap.map { (name, uris) ->
        ImageFolder(name, uris)
    }
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

//        Icon(
//            painter = painterResource(id = android.R.drawable.ic_menu_camera),
//            contentDescription = "Camera",
//            modifier = Modifier.clickable { onCameraClick() }
//        )
    }
}
