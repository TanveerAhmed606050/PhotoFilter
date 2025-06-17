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
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.commonViews.WhiteAppButton
import com.arb.app.photoeffect.ui.theme.BoldFont
import com.arb.app.photoeffect.ui.theme.DarkBlue
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme

@Composable
fun StyleSelectionScreen(navController: NavController) {
    val styleEffectList = mutableListOf(
        R.drawable.style_transfer, R.drawable.style_transfer_2,
        R.drawable.art_filter, R.drawable.art_filter_2,
        R.drawable.cartonize, R.drawable.cartonize_2,
        R.drawable.style_transfer, R.drawable.style_transfer_2,
        R.drawable.art_filter, R.drawable.art_filter_2,
    )
    StyleSelectionUI(styleList = styleEffectList,
        onCancelClick = {
            navController.popBackStack()
        },
        onContinueBtnClick = {
            navController.navigate(Screen.UploadPhotoScreen.route)
        })
}

@Composable
fun StyleSelectionUI(
    styleList: List<Int>,
    onCancelClick: () -> Unit,
    onContinueBtnClick: () -> Unit,
) {
    Box(
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
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(bottom = 80.dp)
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
                text = stringResource(id = R.string.select_style),
                color = DarkBlue,
                fontFamily = BoldFont,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            GridLayout(effectList = styleList, gridCell = 2)
        }
        WhiteAppButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            buttonText = stringResource(id = R.string.continue_1),
            onButtonClick = { onContinueBtnClick() })
    }
}

@Preview
@Composable
fun StyleSelectionPreview() {
    PhotoEffectTheme {
        StyleSelectionUI(styleList = listOf(), onCancelClick = {}, onContinueBtnClick = {})
    }
}