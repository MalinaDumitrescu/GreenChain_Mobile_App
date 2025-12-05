package com.greenchain.feature.homepage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.greenchain.feature.homepage.data.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreatePostUiState(
    val isPosting: Boolean = false,
    val error: String? = null,
    val postCreated: Boolean = false
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState

    fun createPost(text: String, imageUri: Uri?) {
        val authorId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = CreatePostUiState(isPosting = true)
            try {
                postRepository.createPost(authorId, text, imageUri)
                _uiState.value = CreatePostUiState(postCreated = true)
            } catch (e: Exception) {
                _uiState.value = CreatePostUiState(error = e.message)
            }
        }
    }
}
