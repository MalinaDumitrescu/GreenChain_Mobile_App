package com.greenchain.app.navigation

sealed class Routes(
    val route: String,
    val label: String
) {
    data object Onboarding  : Routes("onboarding", "Onboarding")
    data object Auth        : Routes("auth", "Auth")
    data object Home        : Routes("home", "Home")
    data object Map         : Routes("map", "Map")
    data object Scan        : Routes("scan", "Scan")
    data object Leaderboard : Routes("leaderboard", "Leaderboard")
    data object Profile     : Routes("profile", "Profile")

    data object EditProfile : Routes("edit_profile", "Edit Profile")

    companion object {
        val bottomBar = listOf(Home, Map, Scan, Leaderboard, Profile)
    }
}
