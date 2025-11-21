package com.greenchain.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.greenchain.app.ui.theme.Background
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.BrownLight
import com.greenchain.app.ui.theme.GreenPrimary
import androidx.compose.foundation.shape.RoundedCornerShape


data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController
) {
    val faqItems = remember {
        listOf(
            FaqItem(
                "What are Points?",
                "Points are earned every time you recycle a bottle (5 points) or complete eco-friendly quests in the app (15 points). They help you climb the leaderboard and level up your profile."
            ),
            FaqItem(
                "What does my Level mean?",
                "Your Eco Level represents your recycling progress. Levels are based on the total number of points you earn:\n" +
                    "\n" +
                    "• Eco Newbie: 0 – 49 points  \n" +
                    "  You're just getting started! Keep scanning bottles and completing quests.\n" +
                    "\n" +
                    "• Eco Beginner: 50 – 99 points  \n" +
                    "  You're becoming more consistent and environmentally aware.\n" +
                    "\n" +
                    "• Eco Hero: 100 – 149 points  \n" +
                    "  You're making a real impact and showing strong eco responsibility.\n" +
                    "\n" +
                    "You automatically level up as soon as you reach the next point threshold."
            ),
            FaqItem(
                "What are Bottles?",
                "Bottles represent the total number of bottles you have scanned and recycled using the app."
            ),
            FaqItem(
                "What are Friends?",
                "Friends are other users you connect with in the app. You can motivate each other, compare progress and see each other’s achievements."
            ),
            FaqItem(
                "What does Profile Visibility mean?",
                "If your profile is Public, other users can see your name, username, level and basic stats. If it is Private, only you (and approved friends) can see your details."
            )
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & FAQ",
                        color = BrownDark,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BrownDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqItems) { item ->
                FaqCard(item = item)
            }
        }
    }
}

@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenPrimary.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = BrownDark
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = BrownLight
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrownDark,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
