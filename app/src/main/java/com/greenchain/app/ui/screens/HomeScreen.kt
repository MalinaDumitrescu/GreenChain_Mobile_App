package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.greenchain.app.navigation.Routes
import com.greenchain.app.ui.components.*
import com.greenchain.app.ui.components.tokens.GCSpacing
import com.greenchain.feature.homepage.HomeViewModel
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.BrownLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController, // Added NavController
    onNavigateToCreatePost: () -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val quoteText by homeViewModel.quoteText.collectAsState()
    val isQuestCompleted by homeViewModel.isQuestCompleted.collectAsState()
    val dailyQuest by homeViewModel.dailyQuest.collectAsState()
    val posts by homeViewModel.postsFlow.collectAsState(initial = emptyList())
    val currentUserId = homeViewModel.currentUserId
    val currentUserProfile by homeViewModel.currentUserProfile.collectAsState()
    var showQuestDialog by remember { mutableStateOf(false) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    val searchResults by homeViewModel.searchResults.collectAsState()
    val friendRequestStatus by homeViewModel.friendRequestStatus.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val bottlesCount = currentUserProfile?.bottleCount ?: 0
    // ... (rest of the state variables)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                // This is now just a placeholder, the real FABs are in the Box
            }
        ) { _ ->
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    TopBar(
                        onAddFriendsClick = { /* Logic for adding friends */ }
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = GCSpacing.md,
                                end = GCSpacing.md,
                                top = GCSpacing.md,
                                bottom = 0.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(GCSpacing.md)
                    ) {
                        // ... (rest of LazyColumn items)
                        item {
                            if (quoteText != null) {
                                QuoteCard(quote = quoteText!!)
                            } else {
                                QuoteCard()
                            }
                        }
                        item {
                            QuestCard(
                                title = dailyQuest?.title ?: "Quest of the day",
                                progress = if (isQuestCompleted) 1.0f else 0.0f,
                                onView = { /* Logic for viewing quest */ }
                            )
                        }
                        items(posts) { post ->
                            CommunityPostCard(
                                author = post.authorName,
                                time = formatDate(post.timestamp),
                                text = post.text,
                                imageUrl = post.imageUrl ?: "",
                                avatarUrl = post.authorAvatarUrl,
                                isAuthor = post.authorId == currentUserId,
                                onDelete = { homeViewModel.deletePost(post) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress", fontWeight = FontWeight.Medium)
                        Text(questStatusText, fontWeight = FontWeight.Bold, color = BrownDark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = questProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = GreenPrimary,
                        trackColor = GreenPrimary.copy(alpha = 0.2f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    if (isQuestCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quest Completed!",
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            },
            confirmButton = {
                if (!isQuestCompleted) {
                    Button(
                        onClick = {
                            homeViewModel.completeDailyQuest()
                            showQuestDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Mark as Done", color = BrownDark)
                    }
                } else {
                    Button(
                        onClick = { showQuestDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Close", color = BrownDark)
                    }
                }
            }
        }

        // Stack of Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rewards FAB
            FloatingActionButton(
                onClick = { navController.navigate(Routes.Rewards.route) },
                shape = CircleShape,
                containerColor = Color.White, // White background
                contentColor = BrownDark,      // Dark icon
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = "Go to Rewards")
            }

            // Create Post FAB
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                containerColor = GreenPrimary,
                contentColor = BrownDark
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { _ ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                TopBar(
                    onAddFriendsClick = { showAddFriendDialog = true }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = GCSpacing.md,
                            end = GCSpacing.md,
                            top = GCSpacing.md,
                            bottom = 0.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(GCSpacing.md)
                ) {
                    item {
                        if (quoteText != null) {
                            QuoteCard(quote = quoteText!!)
                        } else {
                            QuoteCard()
                        }
                    }

                    item {
                        SavingsCard(
                            bottlesCount = bottlesCount
                        )
                    }

                    item {
                        QuestCard(
                            title = dailyQuest?.title ?: "Quest of the day",
                            progress = questProgress,
                            onView = { showQuestDialog = true }
                        )
                    }

                    items(posts) { post ->
                        CommunityPostCard(
                            author = post.authorName,
                            time = formatDate(post.timestamp),
                            text = post.text,
                            imageUrl = post.imageUrl ?: "",
                            avatarUrl = post.authorAvatarUrl,
                            isAuthor = post.authorId == currentUserId,
                            onDelete = { homeViewModel.deletePost(post) }
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return ""
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    return sdf.format(date)
}


