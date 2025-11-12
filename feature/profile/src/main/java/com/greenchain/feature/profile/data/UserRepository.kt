package com.greenchain.feature.profile.data

import com.greenchain.feature.profile.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore
) {
    private val users = firestore.collection("users")
    private val usernames = firestore.collection("usernames")

    private fun normalize(username: String): String {
        return username.trim().lowercase().replace(Regex("[^a-z0-9._-]"), "")
    }

    /** Verifică dacă username-ul e liber */
    suspend fun isUsernameAvailable(username: String): Boolean {
        val norm = normalize(username)
        val doc = usernames.document(norm).get().await()
        return !doc.exists()
    }

    /**
     * Creează sau actualizează un profil cu username unic.
     * Rezervă username-ul în colecția "usernames" (doc ID = username)
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        val username = normalize(profile.username)
        require(username.isNotEmpty()) { "Username cannot be empty" }

        firestore.runTransaction { tx ->
            val usernameRef = usernames.document(username)
            val existing = tx.get(usernameRef)

            // dacă username-ul e luat de altcineva → eroare
            if (existing.exists() && existing.getString("uid") != profile.uid) {
                throw IllegalStateException("USERNAME_TAKEN")
            }

            // rezervă username-ul pentru uid-ul curent
            tx.set(usernameRef, mapOf("uid" to profile.uid))

            // salvează profilul complet în users/{uid}
            val userRef = users.document(profile.uid)
            tx.set(userRef, profile)
        }.await()
    }

    /** Încarcă profilul utilizatorului curent */
    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = users.document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }
}
