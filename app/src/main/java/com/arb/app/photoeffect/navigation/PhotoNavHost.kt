package com.arb.app.photoeffect.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.arb.app.photoeffect.R
import com.arb.app.photoeffect.ui.screen.effect.ChooseEffectScreen
import com.arb.app.photoeffect.ui.screen.effect.MagicAvatarScreen
import com.arb.app.photoeffect.ui.screen.effect.PhotoFilterScreen
import com.arb.app.photoeffect.ui.screen.effect.StyleSelectionScreen
import com.arb.app.photoeffect.ui.screen.effect.UploadPhotoScreen
import com.arb.app.photoeffect.ui.screen.history.HistoryScreen
import com.arb.app.photoeffect.ui.screen.home.HomeScreen
import com.arb.app.photoeffect.ui.screen.intro.GenderSelectionScreen
import com.arb.app.photoeffect.ui.screen.intro.SplashScreen
import com.arb.app.photoeffect.ui.screen.plus.CustomGalleryScreen
import com.arb.app.photoeffect.ui.screen.plus.PhotoVM
import com.arb.app.photoeffect.ui.screen.plus.ViewImageScreen
import com.arb.app.photoeffect.ui.screen.profile.SettingScreen
import com.arb.app.photoeffect.util.DevicePosture
import com.arb.app.photoeffect.util.PhotoNavigationType
import com.arb.app.photoeffect.util.isBookPosture
import com.arb.app.photoeffect.util.isSeparating

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhotoNavigation(
    navController: NavHostController,
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
) {
    val navigationType: PhotoNavigationType
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = PhotoNavigationType.BOTTOM_NAVIGATION
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = PhotoNavigationType.NAVIGATION_RAIL
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                PhotoNavigationType.NAVIGATION_RAIL
            } else {
                PhotoNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }

        else -> {
            navigationType = PhotoNavigationType.BOTTOM_NAVIGATION
        }
    }

    val bottomMenuList = listOf(
        BottomNavigationItem(
            Screen.HomeScreen.route,
            R.drawable.home_ic,
        ),
        BottomNavigationItem(
            Screen.MagicScreen.route,
            R.drawable.magic_wand,
        ),
        BottomNavigationItem(
            Screen.CustomGalleryScreen.route,
            android.R.drawable.ic_menu_add,
        ),
        BottomNavigationItem(
            Screen.HistoryScreen.route,
            R.drawable.timer_ic,
        ),
        BottomNavigationItem(
            Screen.SettingScreen.route,
            R.drawable.settings_ic,
        ),
    )

    PhotoNavigationWrapper(
        navController = navController,
        bottomMenuList = bottomMenuList,
        navigationType = navigationType
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhotoNavigationWrapper(
    navController: NavHostController,
    bottomMenuList: List<BottomNavigationItem>,
    navigationType: PhotoNavigationType,
) {
    val navigationActions = remember(navController) {
        PSRNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: Screen.LoginScreen.route
    PhotoAppContent(
        bottomMenuList = bottomMenuList,
        navigationType = navigationType,
        navController = navController,
        selectedDestination = selectedDestination,
        navigateToTopLevelDestination = navigationActions::navigateTo
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhotoAppContent(
    modifier: Modifier = Modifier,
    bottomMenuList: List<BottomNavigationItem>,
    navigationType: PhotoNavigationType,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (BottomNavigationItem) -> Unit,
) {
    Row(modifier = modifier.fillMaxSize()) {
        if (bottomMenuList.any { it.route == navController.currentDestination?.route }) AnimatedVisibility(
            visible = navigationType == PhotoNavigationType.NAVIGATION_RAIL
        ) {
        }
        Scaffold(
            bottomBar = {
                if (bottomMenuList.any { it.route == navController.currentDestination?.route }) {
                    AnimatedVisibility(visible = navigationType == PhotoNavigationType.BOTTOM_NAVIGATION) {
                        PhotoBottomNavigationBar(
                            bottomMenuList = bottomMenuList,
                            selectedDestination = selectedDestination,
                            navigateToTopLevelDestination = navigateToTopLevelDestination
                        )
                    }
                }
            }
        ) { innerPadding ->
            PhotoNavHost(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .weight(1f),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhotoNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    val photoVM: PhotoVM = hiltViewModel()
    NavHost(navController, startDestination = Screen.SplashScreen.route) {
        composable(
            route = Screen.SplashScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            SplashScreen(navController)
        }
        composable(
            route = Screen.HomeScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            HomeScreen(navController, photoVM)
        }
        composable(
            route = Screen.SettingScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            SettingScreen(navController)
        }
        composable(
            route = Screen.ChooseEffectScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            ChooseEffectScreen(navController)
        }
        composable(
            route = Screen.GenderSelectionScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            GenderSelectionScreen(navController)
        }
        composable(
            route = Screen.UploadPhotoScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            UploadPhotoScreen(navController)
        }
        composable(
            route = Screen.StyleSelectionScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            StyleSelectionScreen(navController)
        }
        composable(
            route = Screen.MagicScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            MagicAvatarScreen(navController)
        }
        composable(
            route = Screen.PhotoFilterScreen.route + "imageUrl/{data}/{effectType}",
            arguments = listOf(navArgument("data") { type = NavType.StringType },
                navArgument("effectType") { type = NavType.StringType }
            ),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("data")
            val effectType = backStackEntry.arguments?.getString("effectType") ?: ""
            if (url != null) {
                PhotoFilterScreen(navController, photoVM = photoVM, url, effectType)
            }
        }
        composable(
            route = Screen.HistoryScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            HistoryScreen(navController)
        }
        composable(
            route = Screen.CustomGalleryScreen.route + "?data/{effectType}",
            arguments = listOf(navArgument("effectType") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val effectType = backStackEntry.arguments?.getString("effectType")
            CustomGalleryScreen(navController, effectType)
        }
        composable(
            route = Screen.ViewImageScreen.route + "data/{imageUrl}",
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUrl") ?: ""
            ViewImageScreen(navController, imageUri)
        }
    }
}