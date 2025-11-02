package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.app.ui.theme.BrownLight

@Composable
fun QuoteCard(
    quote: String = "The best time to plant a tree was 20 years ago. The second best time is now."
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = GreenPrimary,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = quote,
            color = BrownLight,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
        )
    }
}
