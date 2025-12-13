package com.greenchain.app.navigation

sealed class Routes(
    val route: String,
    val label: String
) {
    data object Onboarding  : Routes("onboarding", "Onboarding")
    data object Auth        : Routes("auth", "Auth")
    data object Setup       : Routes("setup", "Setup")
    data object Home        : Routes("home", "Home")
    data object Map         : Routes("map", "Map")
    data object Scan        : Routes("scan", "Scan")
    data object Leaderboard : Routes("leaderboard", "Leaderboard")
    data object Profile     : Routes("profile", "Profile")
    data object Rewards     : Routes("rewards", "Rewards")

    data object FriendProfile : Routes(
        route = "friend_profile/{uid}",
        label = "Profile"
    )
    data object EditProfile : Routes("edit_profile", "Edit Profile")
    data object CreatePost : Routes("create_post", "Create Post")

    data object Help : Routes("help", "Help")

    companion object {
        // Rewards has been removed from the bottom bar list
        val bottomBar = listOf(Home, Map, Scan, Leaderboard, Profile)
    }
}
