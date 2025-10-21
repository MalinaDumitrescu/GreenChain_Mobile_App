package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable fun HomeScreen() = Placeholder("Home")
@Composable fun ScanScreen() = Placeholder("Scan")
@Composable fun MapScreen() = Placeholder("Map")
@Composable fun FeedScreen() = Placeholder("Feed")
@Composable fun LeaderboardScreen() = Placeholder("Leaderboard")
@Composable fun ProfileScreen() = Placeholder("Profile / Settings")

@Composable
private fun Placeholder(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}
