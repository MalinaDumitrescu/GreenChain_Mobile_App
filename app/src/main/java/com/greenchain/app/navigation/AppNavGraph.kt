package com.greenchain.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greenchain.app.ui.screens.*
// dacă AuthScreen e în modul feature/auth:
import com.greenchain.feature.auth.AuthScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController, startDestination = startDestination) {

        composable(Routes.Onboarding) {
            OnboardingScreen(
                onContinue = {
                    // după onboarding mergem la Auth (dacă vrei direct Home, schimbă ruta aici)
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Auth) {
            AuthScreen(
                onSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home) { HomeScreen() }
        composable(Routes.Scan) { ScanScreen() }
        composable(Routes.Map) { MapScreen() }
        composable(Routes.Feed) { FeedScreen() }
        composable(Routes.Leaderboard) { LeaderboardScreen() }
        composable(Routes.Profile) { ProfileScreen() }
    }
}
