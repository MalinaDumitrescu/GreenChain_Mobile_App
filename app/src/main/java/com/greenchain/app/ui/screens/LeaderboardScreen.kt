package com.greenchain.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.greenchain.app.ui.theme.Background
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.BrownLight
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.feature.leaderboard.ui.LeaderboardViewModel
import com.greenchain.feature.profile.UserProfile

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val leaderboardEntries by viewModel.leaderboard.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header: Icon + Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = BrownDark,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LEADERBOARD",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDark,
                letterSpacing = 2.sp
            )
        }

        // Table Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            colors = CardDefaults.cardColors(containerColor = BrownDark.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RANK",
                    modifier = Modifier.weight(0.2f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "NAME",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "RECYCLED\nBOTTLES",
                    modifier = Modifier.weight(0.3f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }

        // List of ranks
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(leaderboardEntries) { index, entry ->
                LeaderboardItem(index + 1, entry)
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, entry: UserProfile) {
    // Special styling for top 3
    val containerColor = when (rank) {
        1 -> Color(0xFFFFD700).copy(alpha = 0.3f) // Gold
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.3f) // Silver
        3 -> Color(0xFFCD7F32).copy(alpha = 0.3f) // Bronze
        else -> GreenPrimary.copy(alpha = 0.6f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = String.format("%02d", rank),
                modifier = Modifier.weight(0.2f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDark,
                textAlign = TextAlign.Center
            )

            // Name
            Text(
                text = entry.displayName,
                modifier = Modifier.weight(0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrownDark,
                maxLines = 1
            )

            // Bottles
            Text(
                text = entry.bottleCount.toString(),
                modifier = Modifier.weight(0.3f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDark,
                textAlign = TextAlign.Center
            )
        }
    }
}
