package com.greenchain.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greenchain.app.ui.screens.*
import com.greenchain.feature.scan.ui.ScanScreen
import com.greenchain.feature.auth.AuthScreen
import com.greenchain.feature.map.MapScreen
import com.greenchain.feature.profile.ProfileViewModel
import com.greenchain.app.ui.screens.ProfileScreen
import com.greenchain.app.ui.screens.EditProfileScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(
    auth: FirebaseAuth,
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onContinue = {
                    val destination = if (auth.currentUser != null) Routes.Home.route else Routes.Auth.route
                    navController.navigate(destination) {
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
            ScanScreen(
                onCaptured = { uri ->
                    // TODO: hand off to your ViewModel to upload to RoboFlow
                    // scanViewModel.detectWithRoboflow(uri)
                }
            )
        }

        composable(Routes.Map.route) {
            MapScreen()
        }

        composable(Routes.Leaderboard.route) {
            LeaderboardScreen()
        }

        composable(Routes.Profile.route) {
            val vm: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                navController = navController,
                viewModel = vm
            )
        }

        composable(Routes.EditProfile.route) {
            EditProfileScreen(
                navController = navController
            )
        }

        composable(Routes.Help.route) {
            HelpScreen(
                navController = navController
            )
        }
    }
}
