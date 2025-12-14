package com.greenchain.feature.profile.data

import android.net.Uri
import com.greenchain.feature.profile.RedeemedReward
import com.greenchain.feature.profile.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
     * Caută utilizatori după username (prefix search).
     */
    suspend fun searchUsersByUsername(query: String): List<UserProfile> {
        val normQuery = normalize(query)
        if (normQuery.isEmpty()) return emptyList()

        // Căutare prefix: startAt(normQuery) și endAt(normQuery + \uf8ff)
        // Atenție: Asta necesită ca documentele din 'users' să aibă un câmp 'username' sau să căutăm în 'usernames' și apoi să luăm UID-urile.
        // Deoarece 'usernames' are docId = username, putem face un range query pe documentID.

        val snapshot = usernames
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), normQuery)
            .whereLessThan(FieldPath.documentId(), normQuery + "\uf8ff")
            .limit(20)
            .get()
            .await()

        val uids = snapshot.documents.mapNotNull { it.getString("uid") }
        if (uids.isEmpty()) return emptyList()

        return getUsers(uids)
    }

    /**
     * Creează sau actualizează un profil cu username unic.
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        val username = normalize(profile.username)
        require(username.isNotEmpty()) { "Username cannot be empty" }

        firestore.runTransaction { tx ->
            val usernameRef = usernames.document(username)
            val existing = tx.get(usernameRef)

            if (existing.exists() && existing.getString("uid") != profile.uid) {
                throw IllegalStateException("USERNAME_TAKEN")
            }

            tx.set(usernameRef, mapOf("uid" to profile.uid))
            val userRef = users.document(profile.uid)
            tx.set(userRef, profile)
        }.await()
    }

    /** Actualizează profilul (alias pentru saveUserProfile sau update parțial) */
    suspend fun updateUserProfile(profile: UserProfile) {
        saveUserProfile(profile)
    }

    suspend fun createInitialProfileIfNeeded(profile: UserProfile) {
        val doc = users.document(profile.uid).get().await()
        if (!doc.exists()) {
            users.document(profile.uid).set(profile).await()
        }
    }

    /** Încarcă profilul utilizatorului curent (One-shot) */
    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = users.document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }

    /**
     * Returnează profilul utilizatorului ca un flux în timp real (Flow).
     */
    fun getUserProfileFlow(uid: String): Flow<UserProfile?> {
        return users.document(uid).snapshots().map { it.toObject<UserProfile>() }
    }

    /** Caută un utilizator după email */
    suspend fun getUserByEmail(email: String): UserProfile? {
        val snapshot = users.whereEqualTo("email", email).limit(1).get().await()
        return snapshot.documents.firstOrNull()?.toObject(UserProfile::class.java)
    }

    /** Adaugă un prieten */
    suspend fun addFriend(userId: String, friendId: String) {
        users.document(userId).update("friends", FieldValue.arrayUnion(friendId)).await()
    }

    /** Trimite o cerere de prietenie */
    suspend fun sendFriendRequest(fromUserId: String, toUserId: String) {
        users.document(toUserId).update("friendRequests", FieldValue.arrayUnion(fromUserId)).await()
    }

    /** Acceptă o cerere de prietenie */
    suspend fun acceptFriendRequest(currentUserId: String, friendId: String) {
        firestore.runTransaction { tx ->
            val currentUserRef = users.document(currentUserId)
            val friendRef = users.document(friendId)

            tx.update(currentUserRef, "friendRequests", FieldValue.arrayRemove(friendId))
            tx.update(currentUserRef, "friends", FieldValue.arrayUnion(friendId))
            tx.update(friendRef, "friends", FieldValue.arrayUnion(currentUserId))
        }.await()
    }

    /** Refuză o cerere de prietenie */
    suspend fun declineFriendRequest(currentUserId: String, friendId: String) {
        users.document(currentUserId).update("friendRequests", FieldValue.arrayRemove(friendId)).await()
    }

    /** Șterge un prieten */
    suspend fun removeFriend(userId: String, friendId: String) {
        firestore.runTransaction { tx ->
            val userRef = users.document(userId)
            val friendRef = users.document(friendId)

            tx.update(userRef, "friends", FieldValue.arrayRemove(friendId))
            tx.update(friendRef, "friends", FieldValue.arrayRemove(userId))
        }.await()
    }

    suspend fun getUsers(uids: List<String>): List<UserProfile> {
        if (uids.isEmpty()) return emptyList()
        val result = mutableListOf<UserProfile>()
        uids.chunked(10).forEach { chunk ->
            val snapshot = users.whereIn(FieldPath.documentId(), chunk).get().await()
            result.addAll(snapshot.toObjects(UserProfile::class.java))
        }
        return result
    }

    suspend fun redeemCashReward(uid: String, rewardId: String, description: String, pointsCost: Int) {
        val userRef = users.document(uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val userProfile = snapshot.toObject(UserProfile::class.java)
                ?: throw IllegalStateException("User not found")

            if (userProfile.points < pointsCost) {
                throw IllegalStateException("INSUFFICIENT_POINTS")
            }

            val newPoints = userProfile.points - pointsCost
            val newReward = RedeemedReward(
                rewardId = rewardId,
                description = description,
                pointsCost = pointsCost,
                redeemedAt = System.currentTimeMillis()
            )

            transaction.update(userRef, "points", newPoints)
            transaction.update(userRef, "redeemedRewards", FieldValue.arrayUnion(newReward))
        }.await()
    }

    suspend fun resetAllUsersStats() {
        val snapshot = users.get().await()
        for (doc in snapshot.documents) {
            users.document(doc.id).update(mapOf(
                "points" to 0,
                "bottleCount" to 0
            ))
        }
    }

    /** Uploads a profile photo to Firebase Storage and returns the download URL */
    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String {
        val ref = storage.reference.child("profilePictures/$uid")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    /** Deletes the profile photo from Storage and updates Firestore */
    suspend fun deleteProfilePhoto(uid: String) {
        // 1. Delete from Storage
        val ref = storage.reference.child("profilePictures/$uid")
        try {
            ref.delete().await()
        } catch (e: Exception) {
            // ignore
        }

        // 2. Update Firestore
        users.document(uid).update("photoUrl", "").await()
    }

    suspend fun deleteUserProfile(uid: String) {
        val userProfile = getUserProfile(uid)
        val normalizedUsername = userProfile?.username?.let { normalize(it) }

        firestore.runTransaction { transaction ->
            // 1. Delete user document from 'users' collection
            val userRef = users.document(uid)
            transaction.delete(userRef)

            // 2. Delete username from 'usernames' collection
            if (normalizedUsername?.isNotEmpty() == true) {
                val usernameRef = usernames.document(normalizedUsername)
                transaction.delete(usernameRef)
            }
        }.await()
    }
}
