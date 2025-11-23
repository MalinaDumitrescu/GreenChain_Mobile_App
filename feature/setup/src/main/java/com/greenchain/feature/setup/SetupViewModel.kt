package com.greenchain.feature.setup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.core.data.auth.AuthRepository
import com.greenchain.feature.profile.UserProfile
import com.greenchain.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val name: String = "",
    val username: String = "",
    val description: String = "",
    val photoUri: Uri? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(SetupUiState())
    val ui = _ui.asStateFlow()

    fun onName(name: String) {
        _ui.value = _ui.value.copy(name = name)
    }

    fun onUsername(username: String) {
        _ui.value = _ui.value.copy(username = username)
    }

    fun onDescription(description: String) {
        _ui.value = _ui.value.copy(description = description)
    }

    fun onPhotoUriSelected(uri: Uri) {
        _ui.value = _ui.value.copy(photoUri = uri)
    }

    fun save() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            try {
                val currentUser = authRepository.currentUser ?: throw IllegalStateException("User not logged in")
                val photoUrl = _ui.value.photoUri?.let {
                    userRepository.uploadProfilePhoto(currentUser.uid, it)
                } ?: "https://firebasestorage.googleapis.com/v0/b/greenchain-1134d.firebasestorage.app/o/avatar_greenchain.jpg?alt=media&token=eb85f1c9-1db7-4d7b-9e3e-491f09cf5ddb"

                val profile = UserProfile(
                    uid = currentUser.uid,
                    email = currentUser.email ?: "",
                    name = _ui.value.name,
                    username = _ui.value.username,
                    description = _ui.value.description,
                    photoUrl = photoUrl
                )
                userRepository.saveUserProfile(profile)
                _ui.value = _ui.value.copy(loading = false, success = true)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.message)
            }
        }
    }
}
