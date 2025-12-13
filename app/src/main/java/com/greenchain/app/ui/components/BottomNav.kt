package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.greenchain.app.navigation.Routes
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.GreenPrimary

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(BrownDark)
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
    ) {
        // Bar with 5 slots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(88.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Routes.Home.label, Routes.Home.route, Icons.Outlined.Home, selectedRoute == Routes.Home.route, onClick, Modifier.weight(1f))
            NavItem(Routes.Map.label, Routes.Map.route, Icons.Outlined.Map, selectedRoute == Routes.Map.route, onClick, Modifier.weight(1f))

            // Placeholder for the central scan button
            Box(modifier = Modifier.weight(1f).fillMaxHeight())

            NavItem(Routes.Leaderboard.label, Routes.Leaderboard.route, Icons.Outlined.EmojiEvents, selectedRoute == Routes.Leaderboard.route, onClick, Modifier.weight(1f))
            NavItem(Routes.Profile.label, Routes.Profile.route, Icons.Outlined.Person, selectedRoute == Routes.Profile.route, onClick, Modifier.weight(1f))
        }

        // Scan Button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-26).dp)
                .size(76.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
                .background(GreenPrimary)
                .clickable { onClick(Routes.Scan.route) }
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = Routes.Scan.label,
                tint = BrownDark,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    route: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) GreenPrimary else Color(0xFFDDE5B6)

    Box(
        modifier = modifier.clickable { onClick(route) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(30.dp)
        )
    }
}
