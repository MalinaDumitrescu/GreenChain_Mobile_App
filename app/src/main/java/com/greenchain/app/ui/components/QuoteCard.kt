package com.greenchain.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.BrownLight

@Composable
fun QuoteCard(
    quote: String = "The best time to plant a tree was 20 years ago. The second best time is now."
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            // un bej cald, ca o foaie de hârtie
            containerColor = Color(0xFFFEF6E8)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // mic titlu deasupra
            Text(
                text = "Eco thought of the day",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                    color = BrownLight
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // citatul propriu-zis
            Text(
                text = "“$quote”",
                color = BrownDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            )
        }
    }
}
