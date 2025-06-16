package com.arb.app.photoeffect.ui.screen.intro

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.arb.app.photoeffect.ui.theme.RegularFont

@Composable
fun GenderSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedGender by remember { mutableStateOf("") }
    GenderSelectionUI(selectedGender,
        onFemaleClick = {
            selectedGender = context.getString(R.string.female)
        },
        onMaleClick = {
            selectedGender = context.getString(R.string.male)
        },
        continueBtnClick = {
            if (selectedGender.isEmpty()) {
                Toast.makeText(context, "Please Select a gender", Toast.LENGTH_SHORT).show()
                return@GenderSelectionUI
            }
            navController.navigate(Screen.StyleSelectionScreen.route)
        },
        onBackClick = {
            navController.popBackStack()
        })
}

@Composable
fun GenderSelectionUI(
    selectedGender: String,
    onFemaleClick: () -> Unit,
    onMaleClick: () -> Unit,
    continueBtnClick: () -> Unit,
    onBackClick: () -> Unit,
) {
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
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        Icon(
            painter = painterResource(id = R.drawable.back_ic),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
                .clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(id = R.string.pick_gender),
            color = DarkBlue,
            fontFamily = BoldFont,
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.better_effect),
            color = Color.White,
            fontFamily = RegularFont,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onFemaleClick() }
                .border(
                    width = if (selectedGender == stringResource(id = R.string.female)) 2.dp else 1.dp,
                    color = if (selectedGender == stringResource(id = R.string.female)) Color.White else Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.female),
                color = Color.White.copy(0.8f),
                fontFamily = RegularFont,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.style_transfer),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(70.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onMaleClick() }
                .border(
                    width = if (selectedGender == stringResource(id = R.string.male)) 2.dp else 1.dp,
                    color = if (selectedGender == stringResource(id = R.string.male)) Color.White else Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.male),
                color = Color.White.copy(0.8f),
                fontFamily = RegularFont,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.potrait_cutout),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(70.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 32.dp), verticalArrangement = Arrangement.Bottom
        ) {
            WhiteAppButton(buttonText = stringResource(id = R.string.continue_1),
                onButtonClick = {
                    continueBtnClick()
                })
        }
    }
}

@Preview
@Composable
fun GenderSelectionPreview() {
    PhotoEffectTheme {
        GenderSelectionUI("FeMale",
            onMaleClick = {},
            onFemaleClick = {},
            continueBtnClick = {},
            onBackClick = {})
    }
}