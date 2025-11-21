package com.greenchain.feature.profile

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val points: Int = 0,
    val bottleCount: Int = 0,
    val description: String = "",
    val photoUrl: String = "",
    val visibility: String = "public",
    val friends: List<String> = emptyList(),
    val friendRequests: List<String> = emptyList()
) {
    val displayName: String
        get() = when {
            name.isNotBlank() -> name
            email.isNotBlank() -> email.substringBefore("@")
            else -> "GreenChain User"
        }

    val ecoLevel: String
        get() = when (points) {
            in 0..49 -> "Eco Newbie"
            in 50..99 -> "Eco Beginner"
            in 100..149 -> "Eco Hero"
            else -> "Eco Hero"
        }

}
