package com.arb.app.photoeffect.ui.screen.effect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.navigation.Screen
import com.arb.app.photoeffect.ui.commonViews.WhiteAppButton
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun ChooseEffectScreen(navController: NavController) {
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
    ChooseEffectUI(
        caseEffectList,
        errorList,
        onConfirmBtnClick = {
            navController.navigate(Screen.GenderSelectionScreen.route)
        },
        onBackClick = {
            navController.popBackStack()
        }
    )
}

@Composable
fun ChooseEffectUI(
    effectList: List<Int>, errorList: List<Int>,
    onConfirmBtnClick: () -> Unit,
    onBackClick: () -> Unit,
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            Icon(
                painter = painterResource(id = R.drawable.back_ic),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .size(40.dp)
                    .padding(4.dp)
                    .align(Alignment.Start)

            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.hint_effect),
                color = Color.White,
                fontFamily = RegularFont,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.case_effect),
                color = Color.White,
                fontFamily = RegularFont,
                fontSize = 16.sp
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
            Spacer(modifier = Modifier.height(40.dp))
        }
        WhiteAppButton(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
            buttonText = stringResource(id = R.string.confirm),
            onButtonClick = { onConfirmBtnClick() })
    }
}

@Composable
fun GridLayout(effectList: List<Int>, gridCell: Int = 3) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCell),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 2000.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(effectList.size) { index ->
            AsyncImage(
                model = "imageUrl",
                contentDescription = "title",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                error = painterResource(id = effectList[index]),
                placeholder = painterResource(id = effectList[index])
            )

//            Image(
//                painter = painterResource(id = effectList[index]),
//                contentDescription = "",
//                modifier = Modifier
//                    .height(100.dp)
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Fit
//            )
        }
    }
}

@Preview
@Composable
fun ChooseEffectPreview() {
    PhotoEffectTheme {
        ChooseEffectUI(listOf(), listOf(), onConfirmBtnClick = {}, onBackClick = {})
    }
}