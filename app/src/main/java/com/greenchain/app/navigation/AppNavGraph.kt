package com.greenchain.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greenchain.app.ui.screens.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.route) {

        composable(Routes.Home.route) {
            HomeScreen()
        }

        // composable(Routes.Scan.route) { ... }
        // composable(Routes.Map.route) { ... }
        // composable(Routes.Leaderboard.route) { ... }
        // composable(Routes.Profile.route) { ... }
    }
}
