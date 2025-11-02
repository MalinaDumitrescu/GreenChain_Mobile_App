package com.greenchain.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenchain.app.ui.theme.*
import androidx.compose.foundation.background


@Composable
fun StatsCard(
    stats: List<Pair<String, String>> = listOf(
        "3" to "Bottles recycled today",
        "20" to "Total",
        "4kg" to "CO2 saved"
    ),
    modifier: Modifier = Modifier
) {
    Surface(
        color = GreenPrimary,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stats.forEachIndexed { index, (value, label) ->

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = value,
                        color = BrownDark,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = label,
                        color = BrownDark,
                        style = MaterialTheme.typography.bodyMedium,

                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        softWrap = true
                    )
                }

                if (index < stats.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 9.dp)
                            .width(1.dp)
                            .height(60.dp)
                            .align(Alignment.CenterVertically)
                            .background(BrownDark.copy(alpha = 0.5f))
                    )
                }

            }
        }
    }
}
