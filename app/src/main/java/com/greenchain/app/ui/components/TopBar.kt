package com.greenchain.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
fun TopBar(modifier: Modifier = Modifier) {
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


        Box(
            modifier = Modifier
                .border(width = 2.dp, color = BrownDark, shape = CircleShape)
                .size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = BrownDark,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
