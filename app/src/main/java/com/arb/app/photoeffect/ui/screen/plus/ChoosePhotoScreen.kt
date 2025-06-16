package com.arb.app.photoeffect.ui.screen.plus

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme

@Composable
fun ChoosePhotoScreen(navController: NavController) {
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        )
        { uri: Uri? ->
        }

    LaunchedEffect(Unit) {
        openGallery(context, galleryLauncher, pickImageLauncher)

    }
    ChoosePhotoUI()
}

@Composable
fun ChoosePhotoUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        Text(text = "Home", fontFamily = BoldFont, fontSize = 20.sp, color = Color.White,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

fun openGallery(
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    permissionLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    val permissionCheckResult =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
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
        ChoosePhotoUI()
    }
}