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

    /**
     * Creează profilul inițial dacă nu există, doar în colecția 'users'.
     * Nu setează username-ul în 'usernames', deci username-ul ar trebui să fie gol sau tratat ulterior.
     */
    suspend fun createInitialProfileIfNeeded(profile: UserProfile) {
        val doc = users.document(profile.uid).get().await()
        if (!doc.exists()) {
            users.document(profile.uid).set(profile).await()
        }
    }

    /** Încarcă profilul utilizatorului curent */
    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = users.document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }

    /** Caută un utilizator după email */
    suspend fun getUserByEmail(email: String): UserProfile? {
        val snapshot = users.whereEqualTo("email", email).limit(1).get().await()
        return snapshot.documents.firstOrNull()?.toObject(UserProfile::class.java)
    }

    /** Adaugă un prieten (unidirectional pentru moment: user -> friend) */
    suspend fun addFriend(userId: String, friendId: String) {
        users.document(userId).update("friends", FieldValue.arrayUnion(friendId)).await()
    }

    /** Trimite o cerere de prietenie (adaugă UID-ul curent în friendRequests ale celuilalt) */
    suspend fun sendFriendRequest(fromUserId: String, toUserId: String) {
        users.document(toUserId).update("friendRequests", FieldValue.arrayUnion(fromUserId)).await()
    }

    /** Acceptă o cerere de prietenie (tranzacție) */
    suspend fun acceptFriendRequest(currentUserId: String, friendId: String) {
        firestore.runTransaction { tx ->
            val currentUserRef = users.document(currentUserId)
            val friendRef = users.document(friendId)

            // 1. Elimină friendId din friendRequests ale currentUserId
            tx.update(currentUserRef, "friendRequests", FieldValue.arrayRemove(friendId))

            // 2. Adaugă friendId în friends ale currentUserId
            tx.update(currentUserRef, "friends", FieldValue.arrayUnion(friendId))

            // 3. Adaugă currentUserId în friends ale friendId (prietenie bidirecțională)
            tx.update(friendRef, "friends", FieldValue.arrayUnion(currentUserId))
        }.await()
    }

    /** Refuză (șterge) o cerere de prietenie */
    suspend fun declineFriendRequest(currentUserId: String, friendId: String) {
        users.document(currentUserId).update("friendRequests", FieldValue.arrayRemove(friendId)).await()
    }

    /** Șterge un prieten (bidirecțional) */
    suspend fun removeFriend(userId: String, friendId: String) {
        firestore.runTransaction { tx ->
            val userRef = users.document(userId)
            val friendRef = users.document(friendId)

            tx.update(userRef, "friends", FieldValue.arrayRemove(friendId))
            tx.update(friendRef, "friends", FieldValue.arrayRemove(userId))
        }.await()
    }

    /** Încarcă o listă de utilizatori pe baza listei de UID-uri */
    suspend fun getUsers(uids: List<String>): List<UserProfile> {
        if (uids.isEmpty()) return emptyList()
        val result = mutableListOf<UserProfile>()
        uids.chunked(10).forEach { chunk ->
            val snapshot = users.whereIn(FieldPath.documentId(), chunk).get().await()
            result.addAll(snapshot.toObjects(UserProfile::class.java))
        }
        return result
    }

    /**
     * ADMIN: Resetează scorurile tuturor utilizatorilor la 0.
     * Atenție: Aceasta este o operațiune distructivă!
     */
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

    /** Urcă o poză de profil în Firebase Storage și întoarce download URL-ul. */
    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String {
        val ref = storage.reference.child("profilePictures/$uid")
        ref.putFile(imageUri).await()
        val downloadUri = ref.downloadUrl.await()
        return downloadUri.toString()
    }

    /** Update la profil – practic doar delegă la saveUserProfile */
    suspend fun updateUserProfile(profile: UserProfile) {
        saveUserProfile(profile)
    }

    suspend fun deleteProfilePhoto(uid: String) {
        val ref = storage.reference.child("profilePictures/$uid")
        runCatching { ref.delete().await() } // dacă nu există, ignorăm eroarea
        users.document(uid).update("photoUrl", "").await()
    }

}
