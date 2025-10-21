package com.greenchain.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greenchain.app.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Onboarding
) {
    NavHost(navController, startDestination = startDestination) {
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onContinue = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Onboarding) {
                            inclusive = true
                        }
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
