package com.greenchain.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.greenchain.app.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    onHelpClick: () -> Unit = {}
) {
    val state = viewModel.uiState
    val profile = state.userProfile

    // State pentru Dialogul de Adăugare Prieten
    var showAddFriendDialog by remember { mutableStateOf(false) }
    // State pentru Lista de Prieteni (BottomSheet)
    var showFriendsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // --- DIALOG: Add Friend ---
    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text("Add Friend") },
            text = {
                Column {
                    Text("Enter friend's email to send a request:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.friendEmailQuery,
                        onValueChange = viewModel::onFriendEmailQueryChange,
                        label = { Text("Email") },
                        singleLine = true
                    )

                    state.addFriendStatus?.let { status ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = status,
                            color = if (status.contains("sent", true) || status.contains("success", true)) BrownLight else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.sendFriendRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Send Request")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddFriendDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrownLight)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // --- BOTTOM SHEET: Friends List ---
    if (showFriendsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFriendsSheet = false },
            sheetState = sheetState,
            containerColor = Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp) // Padding de jos
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Friends (${state.friendsList.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownDark
                    )
                    IconButton(onClick = {
                        viewModel.onFriendEmailQueryChange("")
                        showAddFriendDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend",
                            tint = BrownLight
                        )
                    }
                }

                if (state.friendsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You have no friends yet.\nInvite them using the button above!",
                            textAlign = TextAlign.Center,
                            color = BrownLight
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.friendsList) { friend ->
                            FriendItem(
                                friend = friend,
                                onDeleteClick = { viewModel.removeFriend(friend.uid) }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- MAIN SCREEN CONTENT ---
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            profile != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    item {
                        // Header
                        Text(
                            text = "Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownDark,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        // 1. MAIN PROFILE CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
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

                        // 2. STATS CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
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

                        // 3. ACCOUNT DETAILS
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
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
                                    Text(text = "About", fontSize = 13.sp, color = BrownDark, fontWeight = FontWeight.Medium)
                                    Spacer(Modifier.height(2.dp))
                                    Text(text = profile.description, fontSize = 13.sp, color = BrownDark)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    // 4. FRIEND REQUESTS (Visible directly)
                    if (state.friendRequestsList.isNotEmpty()) {
                        item {
                            Text(
                                text = "Friend Requests",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrownDark,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                        }
                        items(state.friendRequestsList) { requestUser ->
                            FriendRequestItem(
                                user = requestUser,
                                onAccept = { viewModel.acceptFriendRequest(requestUser.uid) },
                                onDecline = { viewModel.declineFriendRequest(requestUser.uid) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }

                    // 5. MY FRIENDS BUTTON (Replaces the long list)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showFriendsSheet = true },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = BrownLight,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "See my Friends",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrownDark
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = state.friendsList.size.toString(),
                                        fontSize = 16.sp,
                                        color = BrownLight,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForwardIos,
                                        contentDescription = "Open",
                                        tint = BrownLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(22.dp))
                    }

                    // 6. BUTTONS (Now always close to the bottom)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { /* TODO: Edit profile */ },
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
                                onClick = {
                                    viewModel.logout()
                                    navController.navigate(Routes.Auth.route) {
                                        popUpTo(Routes.Home.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
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

                        Text(
                            text = "Help & FAQ about your account",
                            color = BrownLight,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onHelpClick() }
                        )

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(user: UserProfile, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GreenPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    if (user.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.displayName.firstOrNull()?.toString() ?: "?",
                                color = BrownDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.displayName,
                        fontWeight = FontWeight.SemiBold,
                        color = BrownDark,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "wants to be friends",
                        fontSize = 12.sp,
                        color = BrownLight
                    )
                }
            }
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = GreenPrimary
                    )
                }
                IconButton(onClick = onDecline) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: UserProfile, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar simplu
                Surface(
                    shape = CircleShape,
                    color = GreenPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    if (friend.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = friend.photoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = friend.displayName.firstOrNull()?.toString() ?: "?",
                                color = BrownDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = friend.displayName,
                        fontWeight = FontWeight.SemiBold,
                        color = BrownDark,
                        fontSize = 16.sp
                    )
                    if (friend.username.isNotBlank()) {
                        Text(
                            text = "@${friend.username}",
                            fontSize = 12.sp,
                            color = BrownLight
                        )
                    }
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Friend",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
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
