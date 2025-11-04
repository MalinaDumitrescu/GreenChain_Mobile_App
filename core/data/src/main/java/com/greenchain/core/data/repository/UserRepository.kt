package com.greenchain.core.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.greenchain.core.database.dao.UserDao
import com.greenchain.core.database.entity.UserEntity
import com.greenchain.core.model.FirestoreUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(
    private val userDao: UserDao
) {

    fun createOrUpdateUserInFirestoreAndRoom(user: FirebaseUser) {
        val firestoreUser = FirestoreUser(
            id = user.uid,
            name = user.displayName ?: "",
            email = user.email ?: ""
        )

        // 1. Save to Firestore
        Firebase.firestore.collection("users").document(user.uid).set(firestoreUser)

        // 2. Save to Room
        val roomUser = UserEntity(
            id = firestoreUser.id,
            name = firestoreUser.name,
            email = firestoreUser.email
        )
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUser(roomUser)
        }
    }

    fun listenToFirestoreUser(uid: String) {
        Firebase.firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(FirestoreUser::class.java)
                user?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(UserEntity(it.id, it.name, it.email))
                    }
                }
            }
    }
}
