package com.greenchain.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.greenchain.app.R
import com.greenchain.app.ui.theme.BrownDark
import com.greenchain.app.ui.theme.GreenPrimary
import com.greenchain.app.ui.theme.GreenSecondary

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onAddFriendsClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GreenSecondary)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .padding(start = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.greenchain_logo),
                contentDescription = "GreenChain Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(2.2f),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .height(36.dp)
                .width(180.dp)
                .padding(start = 8.dp)
                .background(
                    color = GreenPrimary.copy(alpha = 0.18f),
                    shape = CircleShape
                )
                .padding(horizontal = 12.dp)
                .wrapContentWidth()
                .clickable { onAddFriendsClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Add friends",
                tint = BrownDark,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            androidx.compose.material3.Text(
                text = "Add friends",
                color = BrownDark,
                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyMedium.fontSize
            )
        }
    }
}

