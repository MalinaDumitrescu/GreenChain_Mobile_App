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

import com.greenchain.app.ui.components.BottomNavBar

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
                val currentRoute = backStackEntry?.destination?.route ?: Routes.Home.route

                val start = Routes.Onboarding

                val topLevel = listOf(
                    Routes.Home,
                    Routes.Scan,
                    Routes.Map,
                    Routes.Leaderboard,
                    Routes.Profile
                )

                Scaffold(
                    bottomBar = {
                        // Hide bottom bar on onboarding and auth screens
                        if (currentRoute != Routes.Onboarding.route && currentRoute != Routes.Auth.route) {
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
