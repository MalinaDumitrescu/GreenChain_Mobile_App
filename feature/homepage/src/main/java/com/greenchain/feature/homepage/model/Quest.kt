package com.greenchain.feature.homepage.model

data class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val points: Int = 15
)
