package com.arb.app.photoeffect.ui.screen.plus

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.theme.AppBG
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun ViewImageScreen(navController: NavController, imageUri: String) {
    val context = LocalContext.current
    val decodedUri = remember { Uri.parse(URLDecoder.decode(imageUri, "utf-8")) }
    ViewImageUI(imageUrl = decodedUri,
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
    onFilterClick: (String) -> Unit
) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBG)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Right Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.enhance_photo_2)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 6.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filterList.size) { index ->
                    Text(
                        text = filterList[index], fontSize = 14.sp, color = Color.Black,
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp)
                            .clickable { onFilterClick(filterList[index]) },
                        textAlign = TextAlign.Center,
                        fontFamily = BoldFont
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ViewImageScreenPreview() {
    PhotoEffectTheme {
        ViewImageUI(imageUrl = null,
            onFilterClick = {})
    }
}