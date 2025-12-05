package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.greenchain.app.ui.components.*
import com.greenchain.app.ui.components.tokens.GCSpacing
import com.greenchain.feature.homepage.HomeViewModel
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.app.ui.theme.BrownDark
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
    val posts by homeViewModel.postsFlow.collectAsState(initial = emptyList())
    val currentUserId = homeViewModel.currentUserId

    // State pentru popup-ul Quest
    var showQuestDialog by remember { mutableStateOf(false) }

    val questProgress = if (isQuestCompleted) 1.0f else 0.0f
    val questStatusText = if (isQuestCompleted) "1/1" else "0/1"

    // Popup-ul Quest
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
                                text = "Recycle 1 plastic bottle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrownDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scan and recycle at least one plastic bottle to complete this quest and earn 15 points!",
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
                Button(
                    onClick = { showQuestDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Got it!", color = BrownDark)
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                containerColor = GreenPrimary,
                contentColor = BrownDark
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { paddingValues ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(paddingValues)
        ) {
            Column {
                TopBar()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(GCSpacing.md),
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
                            title = "Quest of the day",
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

                    item { Spacer(Modifier.height(80.dp)) }
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
