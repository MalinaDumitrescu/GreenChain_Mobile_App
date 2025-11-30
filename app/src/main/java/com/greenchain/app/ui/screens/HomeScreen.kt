package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun HomeScreen(onContinue: () -> Unit = {}) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val quoteText by homeViewModel.quoteText.collectAsState()
    val isQuestCompleted by homeViewModel.isQuestCompleted.collectAsState()

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

    Surface(color = MaterialTheme.colorScheme.background) {
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
                item {
                    CommunityPostCard(
                        author = "Janine",
                        time = "1h ago",
                        text = "Weekly reminder to water your plants!!",
                        imageUrl = "https://images.unsplash.com/photo-1501676491271-5e2f2e0d3c59?q=80&w=1200&auto=format",
                        avatarUrl = "https://i.pravatar.cc/100?img=5"
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
