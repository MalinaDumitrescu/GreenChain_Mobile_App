package com.greenchain.model

// This is now a pure data class again, with no ties to the Maps library.
data class RecyclingPoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean = true
)
