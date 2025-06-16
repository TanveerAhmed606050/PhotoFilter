package com.arb.app.photoeffect.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splashScreen")
    data object LoginScreen : Screen("loginScreen")
    data object HomeScreen : Screen("homeScreen")
    data object SettingScreen : Screen("settingScreen")
    data object ChooseEffectScreen : Screen("chooseEffectScreen")
    data object GenderSelectionScreen : Screen("genderScreen")
    data object UploadPhotoScreen : Screen("uploadPhotoScreen")
    data object StyleSelectionScreen : Screen("styleSelectionScreen")
    data object MagicScreen : Screen("magicScreen")
}

class PSRNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: BottomNavigationItem) {
        navController.popBackStack(destination.route, true)
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
}