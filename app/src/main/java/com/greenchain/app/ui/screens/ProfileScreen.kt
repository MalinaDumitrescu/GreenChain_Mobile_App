package com.greenchain.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.greenchain.feature.profile.ProfileViewModel
import com.greenchain.feature.profile.UserProfile
import com.greenchain.app.ui.theme.Background
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.app.ui.theme.BrownLight
import com.greenchain.app.ui.theme.BrownDark
import androidx.compose.ui.graphics.Color
import com.greenchain.app.navigation.Routes


@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    onHelpClick: () -> Unit = {}
) {
    val state = viewModel.uiState
    val profile = state.userProfile

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            profile != null -> {
                ProfileContent(
                    profile = profile,
                    onEditClick = { /* TODO */ },
                    onLogoutClick = {
                        viewModel.logout()

                        navController.navigate(Routes.Auth.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onHelpClick = onHelpClick
                )
            }
        }
    }
}


@Composable
private fun ProfileContent(
    profile: UserProfile,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BrownDark
        )

        Spacer(Modifier.height(20.dp))

        // CARD: poză + nume + email + level
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPrimary
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profile.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = profile.photoUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(10.dp))
                }

                Text(
                    text = profile.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrownDark
                )

                if (profile.username.isNotBlank()) {
                    Text(
                        text = "@${profile.username}",
                        fontSize = 16.sp,
                        color = BrownLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (profile.email.isNotBlank()) {
                    Text(
                        text = profile.email,
                        fontSize = 14.sp,
                        color = BrownLight
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = profile.ecoLevel,
                    fontSize = 14.sp,
                    color = BrownLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // CARD: STATS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPrimary
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn("Points", profile.points.toString())
                StatColumn("Bottles", profile.bottleCount.toString())
                StatColumn("Friends", profile.friends.size.toString())
            }
        }

        Spacer(Modifier.height(16.dp))

        // CARD: ACCOUNT DETAILS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenPrimary
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Account details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrownDark
                )

                Spacer(Modifier.height(8.dp))

                ProfileInfoRow(
                    label = "Visibility",
                    value = profile.visibility.replaceFirstChar { it.uppercase() }
                )

                if (profile.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "About",
                        fontSize = 13.sp,
                        color = BrownDark,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = profile.description,
                        fontSize = 13.sp,
                        color = BrownDark
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary.copy(alpha = 0.8f),
                    contentColor = BrownDark
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Edit profile", fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrownLight,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Medium)
            }
        }


        Spacer(Modifier.height(16.dp))

        // HELP
        Text(
            text = "Help & FAQ about your account",
            color = BrownLight,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onHelpClick() }
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrownDark
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = BrownLight
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = BrownDark
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = BrownLight
        )
    }
}
