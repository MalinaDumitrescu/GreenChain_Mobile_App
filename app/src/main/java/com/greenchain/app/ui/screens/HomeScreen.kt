package com.greenchain.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.greenchain.app.ui.components.*
import com.greenchain.app.ui.components.tokens.GCSpacing
import com.greenchain.feature.homepage.HomeViewModel
import com.greenchain.app.ui.components.QuoteCard


@Composable
fun HomeScreen(onContinue: () -> Unit = {}) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val quoteText by homeViewModel.quoteText.collectAsState()
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
                    QuestCard(title = "Quest of the day", progress = 0.1f, onView = onContinue)
                }
                item {
                    StatsCard(
                        stats = listOf(
                            "3" to "Bottles recycled today",
                            "20" to "Total",
                            "4kg" to "CO2 saved"
                        )
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

