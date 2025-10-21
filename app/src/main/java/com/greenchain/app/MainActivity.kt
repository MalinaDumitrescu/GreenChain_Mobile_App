package com.greenchain.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.greenchain.app.navigation.AppNavGraph
import com.greenchain.app.navigation.Routes
import com.greenchain.app.ui.theme.GreenChainTheme

// todo added later
import androidx.compose.material.icons.filled.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenChainTheme {
                val navController = rememberNavController()
                val topLevel = listOf(
                    Routes.Home,
                    Routes.Scan,
                    Routes.Map,
                    Routes.Feed,
                    Routes.Leaderboard,
                    Routes.Profile
                )

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntry
                            ?.destination
                            ?.route

                        // Hide bottom bar on Onboarding
                        if (currentRoute != Routes.Onboarding) {
                            NavigationBar {
                                topLevel.forEach { route ->
                                    val selected = navController.currentBackStackEntry
                                        ?.destination
                                        ?.hierarchy
                                        ?.any { it.route == route } == true

                                    val icon = when (route) {
                                        Routes.Home -> Icons.Filled.Home
                                        Routes.Scan -> Icons.Filled.CameraAlt
                                        Routes.Map -> Icons.Filled.Map
                                        Routes.Feed -> Icons.Filled.Public
                                        Routes.Leaderboard -> Icons.Filled.EmojiEvents
                                        Routes.Profile -> Icons.Filled.Person
                                        else -> Icons.Filled.Home
                                    }

                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(route) {
                                                popUpTo(Routes.Home) { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = { Icon(icon, contentDescription = route) },
                                        label = { Text(route.replaceFirstChar { it.uppercase() }) }
                                    )
                                }
                            }
                        }
                    }
                ) { inner ->
                    Surface(Modifier.padding(inner)) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
