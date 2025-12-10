package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    onContinue: () -> Unit = {},
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

    val questProgress = if (isQuestCompleted) 1.0f else 0.0f
    val questStatusText = if (isQuestCompleted) "1/1" else "0/1"

    if (showQuestDialog) {
        AlertDialog(
            onDismissRequest = { showQuestDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Daily Quest",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your mission for today:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = dailyQuest?.title ?: "Loading...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrownDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dailyQuest?.description ?: "",
                                style = MaterialTheme.typography.bodySmall
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
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = GreenPrimary,
                        trackColor = GreenPrimary.copy(alpha = 0.2f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    if (isQuestCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quest Completed! 🎉",
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
        )
    }

    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddFriendDialog = false
                homeViewModel.clearSearch()
                searchQuery = ""
            },
            title = { Text("Find Friends", color = BrownDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            homeViewModel.searchUsers(it)
                        },
                        label = { Text("Search by username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            focusedLabelColor = BrownDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(searchResults) { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = user.displayName, fontWeight = FontWeight.Medium, color = BrownDark)
                                    if (user.username.isNotBlank()) {
                                        Text(text = "@${user.username}", style = MaterialTheme.typography.bodySmall, color = BrownLight)
                                    }
                                }
                                Button(
                                    onClick = { homeViewModel.sendFriendRequest(user.uid) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Add", color = BrownDark, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            Divider(color = GreenPrimary.copy(alpha = 0.3f))
                        }
                    }

                    if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                        Text("No users found", style = MaterialTheme.typography.bodySmall, color = BrownLight)
                    }

                    friendRequestStatus?.let { status ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = status,
                            color = if (status.contains("sent", true)) GreenPrimary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAddFriendDialog = false
                        homeViewModel.clearSearch()
                        searchQuery = ""
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrownDark)
                ) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
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
