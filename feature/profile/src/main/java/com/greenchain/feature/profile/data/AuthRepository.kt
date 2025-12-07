package com.greenchain.feature.profile.data

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val user: Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
}
