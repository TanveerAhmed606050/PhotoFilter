package com.arb.app.photoeffect.ui.screen.effect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.ui.commonViews.WhiteAppButton
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.DarkBlue
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun UploadPhotoScreen(navController: NavController) {
    val caseEffectList = mutableListOf(
        R.drawable.style_transfer, R.drawable.style_transfer_2,
        R.drawable.art_filter, R.drawable.art_filter_2,
        R.drawable.cartonize, R.drawable.cartonize_2
    )
    val errorList = listOf(
        R.drawable.colourize, R.drawable.colourize_2,
        R.drawable.edit, R.drawable.edit_2,
        R.drawable.enhance_photo, R.drawable.enhance_photo_2
    )
    UploadPhotoUI(effectList = caseEffectList, errorList = errorList,
        onCancelClick = { navController.popBackStack() })
}

@Composable
fun UploadPhotoUI(
    effectList: List<Int>, errorList: List<Int>,
    onCancelClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize().padding(bottom = 40.dp)
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
        WhiteAppButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            buttonText = "Upload 8-12 Photos",
            onButtonClick = {})
        Column(
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
                    .clickable { onCancelClick() }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.upload_photo),
                color = DarkBlue,
                fontFamily = BoldFont,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.hint_effect),
                color = Color.White,
                fontFamily = RegularFont,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.case_effect),
                color = Color.White,
                fontFamily = RegularFont,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            GridLayout(effectList = effectList)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.case_error),
                color = Color.White,
                fontFamily = RegularFont,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            GridLayout(effectList = errorList)
        }
        WhiteAppButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            buttonText = "Upload 8-12 Photos",
            onButtonClick = {})
    }
}

@Preview
@Composable
fun UploadPhotoScreenPreview() {
    PhotoEffectTheme {
        UploadPhotoUI(effectList = listOf(), errorList = listOf(),
            onCancelClick = {})
    }
}