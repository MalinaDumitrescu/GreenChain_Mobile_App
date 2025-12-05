package com.greenchain.feature.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.greenchain.feature.homepage.data.HomeQuoteRepository
import com.greenchain.feature.homepage.data.PostRepository
import com.greenchain.feature.homepage.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeQuoteRepository: HomeQuoteRepository,
    private val postRepository: PostRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _quoteText = MutableStateFlow<String?>(null)
    val quoteText: StateFlow<String?> = _quoteText.asStateFlow()

    private val _isQuestCompleted = MutableStateFlow(false)
    val isQuestCompleted: StateFlow<Boolean> = _isQuestCompleted.asStateFlow()

    // Flow pentru postari
    val postsFlow = postRepository.getPostsFlow()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadRandomQuote()
        observeQuestStatus()
    }

    fun loadRandomQuote() {
        viewModelScope.launch {
            _quoteText.value = homeQuoteRepository.getRandomQuote().text
        }
    }

    private fun observeQuestStatus() {
        val uid = auth.currentUser?.uid ?: return

        val docRef = firestore.collection("users").document(uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

            val lastDate = snapshot.getString("lastDailyQuestDate")
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            _isQuestCompleted.value = (lastDate == today)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            postRepository.deletePost(post)
        }
    }
}
