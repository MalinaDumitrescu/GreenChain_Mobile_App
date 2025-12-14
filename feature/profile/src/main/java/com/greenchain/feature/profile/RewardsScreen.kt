package com.greenchain.feature.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Using a local drawable resource for the logo
data class AvailableReward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    @DrawableRes val logoResId: Int
)

// Correctly referencing drawable resources. This will show an error until the images are moved to this module's drawable folder.
val availableRewards = listOf(
    AvailableReward("lidl_5", "5 RON at Lidl", "5 RON Voucher", 450, R.drawable.lidl_logo),
    AvailableReward("kaufland_10", "10 RON at Kaufland", "10 RON Voucher", 500, R.drawable.kaufland_logo),
    AvailableReward("auchan_5", "5 RON at Auchan", "5 RON Voucher", 480, R.drawable.auchan_logo)
)

@Composable
fun RewardsScreen(
    viewModel: EditProfileViewModel,
    navController: NavController
) {
    val uiState by viewModel.ui.collectAsState()
    val userProfile = uiState.profile

    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { PointsHeader(userProfile.points) }

        item {
            Text("Available Rewards", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }

        items(availableRewards) { reward ->
            RewardCard(
                reward = reward,
                userPoints = userProfile.points,
                isRedeeming = uiState.isSaving,
                onRedeemClick = { viewModel.redeemReward(reward.id, reward.title, reward.pointsCost) }
            )
        }

        if (userProfile.redeemedRewards.isNotEmpty()) {
            item {
                Text("My Rewards", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            }
            items(userProfile.redeemedRewards.sortedByDescending { it.redeemedAt }) { redeemed ->
                RedeemedRewardCard(redeemed)
            }
        }
    }
}

@Composable
fun PointsHeader(points: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Points", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.height(4.dp))
        Text("$points", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun RewardCard(
    reward: AvailableReward,
    userPoints: Int,
    isRedeeming: Boolean,
    onRedeemClick: () -> Unit
) {
    val canAfford = userPoints >= reward.pointsCost
    val cardAlpha = if (canAfford) 1f else 0.7f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = reward.logoResId),
                contentDescription = "${reward.title} logo",
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(reward.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSecondary.copy(alpha = cardAlpha))
                Text(reward.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = cardAlpha))
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onRedeemClick,
                enabled = canAfford && !isRedeeming,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (isRedeeming) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${reward.pointsCost}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "Points",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RedeemedRewardCard(reward: RedeemedReward) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Star, contentDescription = "Redeemed", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(reward.description, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("Redeemed on: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(reward.redeemedAt))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
        }
    }
}
