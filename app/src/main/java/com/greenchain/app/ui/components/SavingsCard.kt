package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.BrownLight
import com.greenchain.app.ui.theme.GreenPrimary

@Composable
fun SavingsCard(
    bottlesCount: Int,
    modifier: Modifier = Modifier
) {
    val savedMoney = bottlesCount * 0.5f
    val title = if (bottlesCount == 1)
        "Money saved by recycling 1 bottle"
    else
        "Money saved by recycling $bottlesCount bottles"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = BrownDark,
                shape = RoundedCornerShape(18.dp)   // smaller radius
            )
            .padding(horizontal = 18.dp, vertical = 12.dp)   // compact padding
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = GreenPrimary
            )

            Text(
                text = "%.1f lei".format(savedMoney),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFEF6E8)
            )
        }
    }
}


