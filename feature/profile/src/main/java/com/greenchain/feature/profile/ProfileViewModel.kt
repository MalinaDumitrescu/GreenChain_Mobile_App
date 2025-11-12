package com.greenchain.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.profile.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val userProfile: UserProfile? = null,
        val username: String = ""        // 👈 legăm UI direct de acest câmp
    )

    var uiState by mutableStateOf(UiState(isLoading = true))
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            uiState = UiState(isLoading = false, error = "You are not logged in.")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            runCatching {
                userRepo.getUserProfile(currentUser.uid)
            }.onSuccess { loaded ->
                val finalProfile = (loaded ?: UserProfile(
                    uid = currentUser.uid,
                    email = currentUser.email.orEmpty(),
                    name = currentUser.displayName.orEmpty(),
                    photoUrl = currentUser.photoUrl?.toString().orEmpty()
                )).let { p ->
                    // backfill minim pentru câmpuri lipsă
                    p.copy(
                        uid = if (p.uid.isBlank()) currentUser.uid else p.uid,
                        email = if (p.email.isBlank()) currentUser.email.orEmpty() else p.email,
                        name = if (p.name.isBlank()) currentUser.displayName.orEmpty() else p.name
                    )
                }

                uiState = UiState(
                    isLoading = false,
                    userProfile = finalProfile,
                    username = finalProfile.username,  // 👈 populăm câmpul pentru UI
                    error = null
                )
            }.onFailure { e ->
                uiState = UiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load profile."
                )
            }
        }
    }

    /** UI: actualizează textul din câmpul de username (fără validări live) */
    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    /** Opțional: salvează profilul cu noul username (fără verificare live) */
    fun saveUsername(onResult: (Boolean, String?) -> Unit) {
        val current = auth.currentUser ?: return onResult(false, "Not logged in")
        val currentProfile = uiState.userProfile ?: return onResult(false, "No profile loaded")

        val updated = currentProfile.copy(username = uiState.username.trim())
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = runCatching { userRepo.saveUserProfile(updated) }
            uiState = uiState.copy(isLoading = false, userProfile = if (result.isSuccess) updated else currentProfile)
            onResult(result.isSuccess, result.exceptionOrNull()?.localizedMessage)
        }
    }

    fun logout() {
        uiState = uiState.copy(isLoading = true)
        auth.signOut()
        uiState = UiState(isLoading = true, error = null, userProfile = null, username = "")
    }
}
