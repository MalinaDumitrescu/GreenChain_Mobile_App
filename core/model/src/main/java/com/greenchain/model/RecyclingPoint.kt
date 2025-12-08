package com.greenchain.model

// This is a pure data class, with no ties to Firebase or any other library.
data class RecyclingPoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean = true
)
