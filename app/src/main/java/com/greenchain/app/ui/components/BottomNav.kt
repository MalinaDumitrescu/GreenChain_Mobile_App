package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.greenchain.app.navigation.Routes
import com.greenchain.app.ui.theme.BrownDark         // #6C584C
import com.greenchain.app.ui.theme.GreenPrimary      // #DDE5B6

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
        // Bara cu 5 sloturi egale
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(88.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = Routes.Home.label,
                route = Routes.Home.route,
                icon = Icons.Outlined.Home,
                selected = selectedRoute == Routes.Home.route,
                onClick = onClick,
                modifier = Modifier.weight(1f)
            )
            NavItem(
                label = Routes.Map.label,
                route = Routes.Map.route,
                icon = Icons.Outlined.Map,
                selected = selectedRoute == Routes.Map.route,
                onClick = onClick,
                modifier = Modifier.weight(1f)
            )

            //Scan text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onClick(Routes.Scan.route) },
                contentAlignment = Alignment.BottomCenter
            ) {
//                Text(
//                    text = Routes.Scan.label,
//                    color = Color(0xFFDDE5B6),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(bottom = 1.dp)
//                )
            }

            NavItem(
                label = Routes.Leaderboard.label,
                route = Routes.Leaderboard.route,
                icon = Icons.Outlined.EmojiEvents,
                selected = selectedRoute == Routes.Leaderboard.route,
                onClick = onClick,
                modifier = Modifier.weight(1f)
            )
            NavItem(
                label = Routes.Profile.label,
                route = Routes.Profile.route,
                icon = Icons.Outlined.Person,
                selected = selectedRoute == Routes.Profile.route,
                onClick = onClick,
                modifier = Modifier.weight(1f)
            )
        }

        //Scan imagine
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
        modifier = modifier
            .clickable { onClick(route) },
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
