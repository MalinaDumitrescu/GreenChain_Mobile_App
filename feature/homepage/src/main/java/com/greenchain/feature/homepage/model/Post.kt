package com.greenchain.feature.homepage.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp
    val timestamp: Date? = null
)
