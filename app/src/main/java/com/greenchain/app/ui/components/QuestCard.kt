package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.greenchain.app.ui.theme.*

@Composable
fun QuestCard(
    title: String = "Quest of the day",
    progress: Float = 0.0f,
    onView: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = GreenPrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = BrownDark,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                Button(
                    onClick = onView,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrownLight,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("View", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progressText = if (progress >= 1.0f) "1/1" else "0/1"

            Text(
                text = progressText,
                color = BrownLight,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(color = BrownDark.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(color = BrownDark, shape = RoundedCornerShape(50))
                )
            }
        }
    }
}
