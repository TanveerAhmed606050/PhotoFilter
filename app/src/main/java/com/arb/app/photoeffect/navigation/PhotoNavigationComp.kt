package com.arb.app.photoeffect.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.ui.theme.DarkBlue

@Composable
fun PhotoBottomNavigationBar(
    bottomMenuList: List<BottomNavigationItem>,
    selectedDestination: String,
    navigateToTopLevelDestination: (BottomNavigationItem) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ) {
            bottomMenuList.forEach { photoBottomDestination ->
                if (photoBottomDestination.icon == android.R.drawable.ic_menu_add) {
                    Spacer(modifier = Modifier.weight(1f)) // Reserve space for center icon
                } else {
                    NavigationBarItem(
                        selected = selectedDestination == photoBottomDestination.route,
                        onClick = { navigateToTopLevelDestination(photoBottomDestination) },
                        icon = {
                            Icon(
                                painter = painterResource(id = photoBottomDestination.icon),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                        },
                        colors = NavigationBarItemColors(
                            selectedIndicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(0.5f),
                            unselectedTextColor = Color.White.copy(0.5f),
                            disabledIconColor = Color.White.copy(0.5f),
                            disabledTextColor = Color.White.copy(0.5f)
                        )
                    )
                }
            }
        }
// Center Floating Icon
        val centerItem = bottomMenuList.find { it.icon == android.R.drawable.ic_menu_add }
        if (centerItem != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-80).dp) // Moves it upward to overlap
                    .clickable { navigateToTopLevelDestination(centerItem) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus_circle),
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}
