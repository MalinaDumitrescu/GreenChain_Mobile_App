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
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onContinue = {
                    // după onboarding mergem la Auth (dacă vrei direct Home, schimbă ruta aici)
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Auth.route) {
            AuthScreen(
                onSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen()
        }

        composable(Routes.Scan.route) {
            ScanScreen()
        }

        composable(Routes.Map.route) {
            MapScreen()
        }

        composable(Routes.Leaderboard.route) {
            LeaderboardScreen()
        }

        composable(Routes.Profile.route) {
            ProfileScreen()
        }
    }
}
