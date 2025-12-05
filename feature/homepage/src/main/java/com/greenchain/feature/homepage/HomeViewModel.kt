package com.greenchain.feature.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.greenchain.feature.homepage.data.HomeQuoteRepository
import com.greenchain.feature.homepage.data.PostRepository
import com.greenchain.feature.homepage.model.Post
import com.greenchain.feature.profile.data.UserRepository
import com.greenchain.feature.profile.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeQuoteRepository: HomeQuoteRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _quoteText = MutableStateFlow<String?>(null)
    val quoteText: StateFlow<String?> = _quoteText.asStateFlow()

    private val _isQuestCompleted = MutableStateFlow(false)
    val isQuestCompleted: StateFlow<Boolean> = _isQuestCompleted.asStateFlow()

    // Search Friends State
    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults.asStateFlow()

    private val _friendRequestStatus = MutableStateFlow<String?>(null)
    val friendRequestStatus: StateFlow<String?> = _friendRequestStatus.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    // Flow pentru postari
    val postsFlow = postRepository.getPostsFlow()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadRandomQuote()
        observeQuestStatus()
        observeCurrentUser()
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

    private fun observeCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.getUserProfileFlow(uid).collectLatest { profile ->
                _currentUserProfile.value = profile
            }
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            postRepository.deletePost(post)
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = userRepository.searchUsersByUsername(query)
                // Filter out current user
                val uid = auth.currentUser?.uid
                _searchResults.value = results.filter { it.uid != uid }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun sendFriendRequest(toUserId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.sendFriendRequest(uid, toUserId)
                _friendRequestStatus.value = "Request sent!"
            } catch (e: Exception) {
                _friendRequestStatus.value = "Failed: ${e.message}"
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _friendRequestStatus.value = null
    }
}
