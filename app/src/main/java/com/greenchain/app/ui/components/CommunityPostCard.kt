package com.greenchain.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.greenchain.app.ui.theme.BrownDark   // #6C584C
import com.greenchain.app.ui.theme.BrownLight  // #A98467
import com.greenchain.app.ui.theme.GreenPrimary // #DDE5B6

@Composable
fun CommunityPostCard(
    author: String,
    time: String,
    text: String,
    imageUrl: String,
    avatarUrl: String,
    modifier: Modifier = Modifier,
    isAuthor: Boolean = false,
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = GreenPrimary,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp)) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    SubcomposeAsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        when (painter.state) {
                            is coil.compose.AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent(contentScale = ContentScale.Crop)
                            }
                            else -> {

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(BrownLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials(author),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            author,
                            color = BrownDark,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            time,
                            color = BrownLight,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (isAuthor) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = BrownLight
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))


            Text(
                text = text,
                color = BrownDark,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(12.dp))

            if (imageUrl.isNotEmpty()) {
                Surface(
                    color = Color(0xFFF0EAD2),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(192.dp)
                    )
                }
            }
        }
    }
}

private fun initials(name: String): String =
    name.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
