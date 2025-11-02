package com.greenchain.app.navigation
//
//object Routes {
//    const val Onboarding = "onboarding"
//    const val Home = "home"
//    const val Scan = "scan"
//    const val Map = "map"
//    const val Feed = "feed"
//    const val Leaderboard = "leaderboard"
//    const val Profile = "profile"
//}


sealed class Routes(
    val route: String,
    val label: String
) {
    data object Onboarding  : Routes("onboarding", "Onboarding")
    data object Home        : Routes("home", "Home")
    data object Map         : Routes("map", "Map")
    data object Scan        : Routes("scan", "Scan")
    data object Leaderboard : Routes("leaderboard", "Leaderboard")
    data object Profile     : Routes("profile", "Profile")

    companion object {
        val bottomBar = listOf(Home, Map, Scan, Leaderboard, Profile)
    }
}

