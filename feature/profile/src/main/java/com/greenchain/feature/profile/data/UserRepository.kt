package com.greenchain.feature.profile.data

import android.net.Uri
import com.greenchain.feature.profile.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val users = firestore.collection("users")
    private val usernames = firestore.collection("usernames")

    suspend fun searchUsersByUsername(query: String): List<UserProfile> {
        if (query.isBlank()) {
            return emptyList()
        }
        val normalizedQuery = normalize(query)
        val snapshot = users
            .whereGreaterThanOrEqualTo("username", normalizedQuery)
            .whereLessThanOrEqualTo("username", normalizedQuery + '\uf8ff')
            .orderBy("username")
            .limit(20)
            .get()
            .await()
        return snapshot.toObjects(UserProfile::class.java)
    }

    private fun normalize(username: String): String {
        return username.trim().lowercase().replace(Regex("[^a-z0-9._-]"), "")
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        val norm = normalize(username)
        val doc = usernames.document(norm).get().await()
        return !doc.exists()
    }

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

    suspend fun createInitialProfileIfNeeded(profile: UserProfile) {
        val doc = users.document(profile.uid).get().await()
        if (!doc.exists()) {
            users.document(profile.uid).set(profile).await()
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = users.document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }

    suspend fun getUserByEmail(email: String): UserProfile? {
        val snapshot = users.whereEqualTo("email", email).limit(1).get().await()
        return snapshot.documents.firstOrNull()?.toObject(UserProfile::class.java)
    }

    suspend fun addFriend(userId: String, friendId: String) {
        users.document(userId).update("friends", FieldValue.arrayUnion(friendId)).await()
    }

    suspend fun sendFriendRequest(fromUserId: String, toUserId: String) {
        users.document(toUserId).update("friendRequests", FieldValue.arrayUnion(fromUserId)).await()
    }

    suspend fun acceptFriendRequest(currentUserId: String, friendId: String) {
        firestore.runTransaction { tx ->
            val currentUserRef = users.document(currentUserId)
            val friendRef = users.document(friendId)

            tx.update(currentUserRef, "friendRequests", FieldValue.arrayRemove(friendId))

            tx.update(currentUserRef, "friends", FieldValue.arrayUnion(friendId))

            tx.update(friendRef, "friends", FieldValue.arrayUnion(currentUserId))
        }.await()
    }

    suspend fun declineFriendRequest(currentUserId: String, friendId: String) {
        users.document(currentUserId).update("friendRequests", FieldValue.arrayRemove(friendId)).await()
    }

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

    suspend fun resetAllUsersStats() {
        val snapshot = users.get().await()
        for (doc in snapshot.documents) {
            users.document(doc.id).update(mapOf(
                "points" to 0,
                "bottleCount" to 0
            ))
        }
    }

    private fun profilePhotoRef(uid: String) =
        storage.reference.child("profilePictures/$uid")

    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String {
        val ref = storage.reference.child("profilePictures/$uid")
        ref.putFile(imageUri).await()
        val downloadUri = ref.downloadUrl.await()
        return downloadUri.toString()
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        saveUserProfile(profile)
    }

    suspend fun deleteProfilePhoto(uid: String) {
        val ref = storage.reference.child("profilePictures/$uid")
        runCatching { ref.delete().await() }
        users.document(uid).update("photoUrl", "").await()
    }

    suspend fun addBottleAndPoints(uid: String) {
        users.document(uid).update(
            mapOf(
                "bottleCount" to FieldValue.increment(1),
                "points" to FieldValue.increment(5)
            )
        ).await()
    }

    suspend fun addQuestPoints(uid: String) {
        users.document(uid)
            .update("points", FieldValue.increment(15))
            .await()
    }

}
