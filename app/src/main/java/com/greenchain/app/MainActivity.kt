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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.greenchain.app.ui.components.BottomNavBar

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
                    Routes.Leaderboard,
                    Routes.Profile
                )

                setContent {
                    GreenChainTheme {
                        val navController = rememberNavController()

                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = backStackEntry?.destination?.route ?: Routes.Home.route

                        Scaffold(
                            bottomBar = {

                                if (currentRoute != Routes.Onboarding.route) {
                                    BottomNavBar(
                                        selectedRoute = currentRoute,
                                        onClick = { route ->
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
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
    }
}
