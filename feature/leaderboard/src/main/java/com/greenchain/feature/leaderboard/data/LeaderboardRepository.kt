package com.greenchain.feature.leaderboard.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.greenchain.feature.profile.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LeaderboardRepository(
    private val firestore: FirebaseFirestore
) {
    fun getLeaderboard(): Flow<List<UserProfile>> {
        return firestore.collection("users")
            .orderBy("bottleCount", Query.Direction.DESCENDING) // Sort by Recycled Bottles
            .limit(100)
            .snapshots()
            .map { it.toObjects<UserProfile>() }
    }
}
