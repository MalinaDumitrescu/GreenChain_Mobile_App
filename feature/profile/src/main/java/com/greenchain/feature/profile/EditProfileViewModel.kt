package com.greenchain.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.core.network.di.AuthStateProvider
import com.greenchain.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val profile: UserProfile? = null,
    val isSaving: Boolean = false,   // îl folosim și pentru remove
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repo: UserRepository,
    private val authStateProvider: AuthStateProvider
) : ViewModel() {

    private val _ui = MutableStateFlow(EditProfileUiState())
    val ui: StateFlow<EditProfileUiState> = _ui

    init {
        viewModelScope.launch {
            authStateProvider.authState.collectLatest { user ->
                if (user != null) {
                    loadProfile(user.uid)
                }
            }
        }
    }

    private fun loadProfile(uid: String) {
        viewModelScope.launch {
            try {
                val profile = repo.getUserProfile(uid)
                _ui.value = _ui.value.copy(profile = profile, error = null)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(error = e.message)
            }
        }
    }

    fun saveProfile(
        name: String,
        email: String,
        username: String,
        description: String,
        newPhotoUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        if (username.isBlank()) {
            _ui.value = _ui.value.copy(error = "Username cannot be empty")
            onResult(false)
            return
        }
        if (email.isBlank()) {
            _ui.value = _ui.value.copy(error = "Email cannot be empty")
            onResult(false)
            return
        }

        val uid = _ui.value.profile?.uid ?: return

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null)

            try {
                val current = _ui.value.profile!!

                val finalPhotoUrl = if (newPhotoUri != null) {
                    repo.uploadProfilePhoto(uid, newPhotoUri)
                } else {
                    current.photoUrl
                }

                val updated = current.copy(
                    name = name,
                    email = email,
                    username = username,
                    description = description,
                    photoUrl = finalPhotoUrl
                )

                repo.updateUserProfile(updated)

                _ui.value = _ui.value.copy(
                    profile = updated,
                    isSaving = false,
                    error = null
                )
                onResult(true)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    isSaving = false,
                    error = e.message
                )
                onResult(false)
            }
        }
    }

    /** Șterge poza de profil (din Storage + din profil) și actualizează UI */
    fun removeProfilePhoto() {
        val uid = _ui.value.profile?.uid ?: return
        val currentProfile = _ui.value.profile!!

        // dacă nu are poză, nu facem nimic
        if (currentProfile.photoUrl.isBlank()) return

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null)
            try {
                // ștergem fișierul + resetăm photoUrl în Firestore
                repo.deleteProfilePhoto(uid)

                // actualizăm și UI-ul local
                val updated = currentProfile.copy(photoUrl = "")
                _ui.value = _ui.value.copy(
                    profile = updated,
                    isSaving = false
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to remove photo."
                )
            }
        }
    }
}
