package com.greenchain.feature.homepage.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.greenchain.feature.homepage.model.Post
import com.greenchain.feature.profile.data.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userRepository: UserRepository
) {

    suspend fun createPost(authorId: String, text: String, imageUri: Uri?) {
        val userProfile = userRepository.getUserProfile(authorId)
            ?: throw IllegalStateException("User profile not found")

        val imageUrl = if (imageUri != null) {
            uploadPostImage(authorId, imageUri)
        } else {
            null
        }

        val post = Post(
            id = UUID.randomUUID().toString(),
            authorId = authorId,
            authorName = userProfile.name,
            authorAvatarUrl = userProfile.photoUrl,
            text = text,
            imageUrl = imageUrl
        )

        firestore.collection("posts").document(post.id).set(post).await()
    }

    private suspend fun uploadPostImage(userId: String, uri: Uri): String {
        val storageRef = storage.reference.child("post_images/$userId/${uri.lastPathSegment}")
        val uploadTask = storageRef.putFile(uri).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    fun getPostsFlow(): Flow<List<Post>> = callbackFlow {
        // Marim limita la 100 pentru a permite filtrarea client-side mai eficienta
        // Intr-o aplicatie de productie, ar trebui folosit 'whereIn' si paginare cu indexi compusi.
        val listener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun deletePost(post: Post) {
        // Delete image from Storage if it exists
        post.imageUrl?.let {
            try {
                storage.getReferenceFromUrl(it).delete().await()
            } catch (e: Exception) {
                // Ignore if deletion fails (e.g. file not found)
            }
        }
        // Delete post from Firestore
        firestore.collection("posts").document(post.id).delete().await()
    }
}
