package com.greenchain.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.greenchain.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _ui = MutableStateFlow(EditProfileUiState())
    val ui: StateFlow<EditProfileUiState> = _ui

    fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
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
        val firebaseUser = auth.currentUser ?: return
        val uid = firebaseUser.uid

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null)

            try {
                val current = _ui.value.profile
                    ?: repo.getUserProfile(uid)
                    ?: UserProfile(uid = uid, email = firebaseUser.email.orEmpty())

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
        val firebaseUser = auth.currentUser ?: return
        val uid = firebaseUser.uid
        val currentProfile = _ui.value.profile ?: return

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
