package com.arb.app.photoeffect.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.arb.app.photoeffect.ui.commonViews.Header
import com.arb.app.photoeffect.ui.theme.PhotoEffectTheme

@Composable
fun SettingScreen(navController: NavController) {
    SettingScreenUI()
}

@Composable
fun SettingScreenUI() {
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
        Header()
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.background(Color.Black, shape = RoundedCornerShape(12.dp))) {
            SettingSimpleRow(
                stringResource(id = R.string.upgrade_pro), R.drawable.pro_plan,
                color = Color.Yellow
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.background(Color.Black, shape = RoundedCornerShape(12.dp))) {
            SettingSimpleRow(
                stringResource(id = R.string.remove_ads), R.drawable.remove_ads
            )
            SettingSimpleRow(
                stringResource(id = R.string.more_app), R.drawable.more_dots
            )
            SettingSimpleRow(
                stringResource(id = R.string.share_app), R.drawable.share_ic
            )
            SettingSimpleRow(
                stringResource(id = R.string.feedback), R.drawable.feedback
            )
            SettingSimpleRow(
                stringResource(id = R.string.faq), R.drawable.faq
            )
            SettingSimpleRow(
                stringResource(id = R.string.privacy_pol), R.drawable.privacy_policy
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun SettingSimpleRow(title: String, icon: Int, color: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = icon), contentDescription = null, modifier = Modifier
                .size(20.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = Color.White)
    }
}

@Preview
@Composable
fun SettingScreenPreview() {
    PhotoEffectTheme {
        SettingScreenUI()
    }
}