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
        val username: String = "",
        val friendEmailQuery: String = "",
        val addFriendStatus: String? = null,
        val friendsList: List<UserProfile> = emptyList(),
        val friendRequestsList: List<UserProfile> = emptyList()
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
            try {
                var profile = userRepo.getUserProfile(currentUser.uid)

                if (profile == null) {
                    val newProfile = UserProfile(
                        uid = currentUser.uid,
                        email = currentUser.email.orEmpty(),
                        name = currentUser.displayName.orEmpty(),
                        photoUrl = currentUser.photoUrl?.toString().orEmpty()
                    )
                    userRepo.createInitialProfileIfNeeded(newProfile)
                    profile = newProfile
                }

                // (Rollback: Am scos migrarea de aici)

                val finalProfile = profile!!.copy(
                    uid = if (profile.uid.isBlank()) currentUser.uid else profile.uid,
                    email = if (profile.email.isBlank()) currentUser.email.orEmpty() else profile.email,
                    name = if (profile.name.isBlank()) currentUser.displayName.orEmpty() else profile.name
                )

                // Load Friends
                val friendsDetails = if (finalProfile.friends.isNotEmpty()) {
                    runCatching { userRepo.getUsers(finalProfile.friends) }.getOrElse { emptyList() }
                } else {
                    emptyList()
                }

                // Load Friend Requests
                val requestsDetails = if (finalProfile.friendRequests.isNotEmpty()) {
                    runCatching { userRepo.getUsers(finalProfile.friendRequests) }.getOrElse { emptyList() }
                } else {
                    emptyList()
                }

                uiState = UiState(
                    isLoading = false,
                    userProfile = finalProfile,
                    username = finalProfile.username,
                    friendsList = friendsDetails,
                    friendRequestsList = requestsDetails,
                    error = null
                )
            } catch (e: Exception) {
                uiState = UiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load profile."
                )
            }
        }
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

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

    // --- Friend functionality ---

    fun onFriendEmailQueryChange(query: String) {
        uiState = uiState.copy(friendEmailQuery = query)
    }

    fun sendFriendRequest() {
        val currentUser = auth.currentUser ?: return
        val email = uiState.friendEmailQuery.trim()

        if (email.isBlank()) {
            uiState = uiState.copy(addFriendStatus = "Please enter an email address")
            return
        }
        if (email == currentUser.email) {
            uiState = uiState.copy(addFriendStatus = "You cannot add yourself as a friend")
            return
        }

        uiState = uiState.copy(isLoading = true, addFriendStatus = null)
        viewModelScope.launch {
            runCatching {
                val friendProfile = userRepo.getUserByEmail(email)
                    ?: throw Exception("User not found with this email")

                if (uiState.userProfile?.friends?.contains(friendProfile.uid) == true) {
                    throw Exception("User is already your friend")
                }
                if (uiState.userProfile?.friendRequests?.contains(friendProfile.uid) == true) {
                     throw Exception("This user already sent you a request. Check requests above.")
                }

                userRepo.sendFriendRequest(currentUser.uid, friendProfile.uid)
                "Friend request sent!"
            }.onSuccess { msg ->
                uiState = uiState.copy(isLoading = false, addFriendStatus = msg, friendEmailQuery = "")
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, addFriendStatus = e.message)
            }
        }
    }

    fun acceptFriendRequest(friendId: String) {
        val currentUser = auth.currentUser ?: return
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepo.acceptFriendRequest(currentUser.uid, friendId)
                loadUserProfile()
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun declineFriendRequest(friendId: String) {
        val currentUser = auth.currentUser ?: return
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepo.declineFriendRequest(currentUser.uid, friendId)
                loadUserProfile()
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun removeFriend(friendId: String) {
        val currentUser = auth.currentUser ?: return
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepo.removeFriend(currentUser.uid, friendId)
                loadUserProfile()
                "Friend removed."
            }.onFailure { e ->
                 uiState = uiState.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun logout() {
        uiState = uiState.copy(isLoading = true)
        auth.signOut()
        uiState = UiState(isLoading = true, error = null, userProfile = null, username = "")
    }

    /**
     * DEV ONLY: Resetează punctele și sticlele tuturor utilizatorilor.
     */
    fun resetAllStatsForDev() {
        viewModelScope.launch {
            runCatching {
                userRepo.resetAllUsersStats()
                loadUserProfile() // Reload UI
            }
        }
    }

    // Salvează tot profilul (name, username, description, photoUrl)
    fun saveProfile(
        name: String,
        username: String,
        description: String,
        photoUrl: String? // null = păstrăm ce era
    ) {
        val current = auth.currentUser ?: return
        val currentProfile = uiState.userProfile ?: return

        val updated = currentProfile.copy(
            name = name.trim(),
            username = username.trim(),
            description = description.trim(),
            photoUrl = photoUrl ?: currentProfile.photoUrl
        )

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = runCatching { userRepo.saveUserProfile(updated) }
            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    userProfile = updated,      // 🔥 ProfileScreen se actualizează imediat
                    username = updated.username
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    // Șterge poza de profil (din Storage + din profil) și actualizează UI
    fun removeProfilePhoto() {
        val current = auth.currentUser ?: return
        val profile = uiState.userProfile ?: return

        if (profile.photoUrl.isBlank()) return  // nu are ce să șteargă

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // apel la repo -> șterge fișierul și resetează photoUrl în Firestore
                userRepo.deleteProfilePhoto(current.uid)

                // actualizăm și UI-ul local
                val updated = profile.copy(photoUrl = "")
                uiState = uiState.copy(
                    isLoading = false,
                    userProfile = updated
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to remove photo."
                )
            }
        }
    }
    fun refreshProfile() {
        loadUserProfile()
    }



}
