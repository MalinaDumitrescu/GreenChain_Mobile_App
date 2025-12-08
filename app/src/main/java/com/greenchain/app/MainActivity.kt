package com.greenchain.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.greenchain.app.navigation.AppNavGraph
import com.greenchain.app.navigation.Routes
import com.greenchain.app.ui.theme.GreenChainTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.navigation.NavDestination.Companion.hierarchy

import com.google.firebase.firestore.FirebaseFirestore
import com.greenchain.core.database.AppDatabase

import com.greenchain.app.ui.components.BottomNavBar
import com.greenchain.core.network.di.AuthStateProvider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var authStateProvider: AuthStateProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GreenChainTheme {
                // Notification Permission Request
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted: Boolean ->
                            if (isGranted) {
                                Log.d("Notifications", "POST_NOTIFICATIONS permission granted")
                            } else {
                                Log.d("Notifications", "POST_NOTIFICATIONS permission denied")
                            }
                        }
                    )
                    LaunchedEffect(Unit) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: Routes.Home.route

                val start = Routes.Onboarding

                val topLevel = listOf(
                    Routes.Home,
                    Routes.Scan,
                    Routes.Map,
                    Routes.Leaderboard,
                    Routes.Profile
                )

                LaunchedEffect(Unit) {
                    authStateProvider.authState.collect { user ->
                        val isOnOnboarding = navController.currentDestination?.route == Routes.Onboarding.route
                        if (user == null && !isOnOnboarding) {
                            navController.navigate(Routes.Auth.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        // Hide bottom bar on onboarding, auth, and setup screens
                        if (currentRoute != Routes.Onboarding.route && currentRoute != Routes.Auth.route && currentRoute != Routes.Setup.route) {
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
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        AppNavGraph(
                            auth = auth,
                            navController = navController,
                            startDestination = start.route
                        )
                    }
                }
            }
        }
    }
}
