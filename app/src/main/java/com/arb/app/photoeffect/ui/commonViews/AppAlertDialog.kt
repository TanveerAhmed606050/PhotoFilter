package com.arb.app.photoeffect.ui.commonViews

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.ui.theme.AppBG
import com.arb.app.photoeffect.ui.theme.DarkBlue
import com.arb.app.photoeffect.ui.theme.boldFont
import com.arb.app.photoeffect.ui.theme.regularFont

@Composable
fun LogoutDialog(
    onDismissRequest: () -> Unit, onOkClick: () -> Unit
) {
    var dialogRotation by remember { mutableFloatStateOf(-90f) }
    LaunchedEffect(Unit) {
        dialogRotation = -90f
        androidx.compose.animation.core.animate(
            initialValue = -90f,
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 600,
                easing = LinearOutSlowInEasing
            )
        ) { value, _ ->
            dialogRotation = value
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .graphicsLayer {
                    rotationX = dialogRotation
                    cameraDistance = 16f * density
                }
                .padding(16.dp)
                .background(AppBG, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp,
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = boldFont,
                    color = DarkBlue,
                )
                Text(
                    text = stringResource(id = R.string.account_logout_warning),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    fontSize = 14.sp,
                    fontFamily = regularFont,
                    color = DarkBlue,
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.DarkGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismissRequest, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.no),
                            fontSize = 14.sp,
                            fontFamily = boldFont,
                            color = DarkBlue
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .width(0.5.dp)
                            .height(50.dp)
                            .background(Color.DarkGray)
                    )
                    TextButton(
                        onClick = onOkClick, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.yes),
                            color = DarkBlue,
                            fontSize = 14.sp,
                            fontFamily = boldFont
                        )
                    }
                }
            }
        }
    }
}
