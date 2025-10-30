package com.greenchain.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import android.util.Log
import com.greenchain.core.database.AppDatabase


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // ✅ ROOM CHECK
//        Log.d("Room", "Room DB: ${database.openHelper.writableDatabase.path}")
//
//        // ✅ FIRESTORE CHECK
//        val db = FirebaseFirestore.getInstance()
//        val testData = hashMapOf("status" to "connected", "timestamp" to System.currentTimeMillis())
//
//        db.collection("testCheck").document("ping")
//            .set(testData)
//            .addOnSuccessListener { Log.d("Firestore", "Write OK") }
//            .addOnFailureListener { e -> Log.e("Firestore", "Write failed", e) }
//
//        db.collection("testCheck").document("ping")
//            .get()
//            .addOnSuccessListener { doc -> Log.d("Firestore", "Read OK: ${doc.data}") }
//            .addOnFailureListener { e -> Log.e("Firestore", "Read failed", e) }

        setContent {
            GreenChainTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()

                // TODO: replace with real DataStore check
                val onboardingDone = false
                val start = when {
                    !onboardingDone -> Routes.Onboarding
                    auth.currentUser == null -> Routes.Auth
                    else -> Routes.Home
                }

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
                        val route = backStackEntry?.destination?.route
                        val hideBar = route == Routes.Onboarding || route == Routes.Auth
                        if (!hideBar) {
                            NavigationBar {
                                topLevel.forEach { r ->
                                    val selected = backStackEntry?.destination
                                        ?.hierarchy
                                        ?.any { dest -> dest.route == r } == true

                                    val icon = when (r) {
                                        Routes.Home -> Icons.Filled.Home
                                        Routes.Scan -> Icons.Filled.CameraAlt
                                        Routes.Map -> Icons.Filled.Map
                                        Routes.Feed -> Icons.Filled.Public
                                        Routes.Leaderboard -> Icons.Filled.EmojiEvents
                                        Routes.Profile -> Icons.Filled.Person
                                        else -> Icons.Filled.Home
                                    }

                                    val labelText = if (r.isNotEmpty())
                                        r.replaceFirstChar { ch ->
                                            if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                                        }
                                    else r

                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(r) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = false
                                                }
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = { Icon(icon, contentDescription = r) },
                                        label = { Text(labelText) }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        AppNavGraph(
                            navController = navController,
                            startDestination = start
                        )
                    }
                }
            }
        }
    }
}
