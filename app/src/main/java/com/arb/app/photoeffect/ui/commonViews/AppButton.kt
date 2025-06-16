package com.arb.app.photoeffect.ui.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arb.app.photoeffect.ui.theme.BoldFont

@Composable
fun WhiteAppButton(
    modifier: Modifier = Modifier, buttonText: String, borderColor: Color = Color.White,
    onButtonClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(30.dp)),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        onClick = { onButtonClick() },
    ) {
        Text(
            text = buttonText,
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = BoldFont
        )
    }
}