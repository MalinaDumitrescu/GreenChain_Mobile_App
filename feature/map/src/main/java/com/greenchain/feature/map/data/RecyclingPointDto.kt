package com.greenchain.feature.map.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class RecyclingPointDto(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    // This annotation maps the 'isActive' property to the 'active' field in Firestore.
    @get:PropertyName("active") @set:PropertyName("active")
    var isActive: Boolean = true
)
