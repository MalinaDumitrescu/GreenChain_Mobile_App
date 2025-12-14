package com.greenchain.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.greenchain.core.network.di.AuthStateProvider
import com.greenchain.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(FlowPreview::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository,
    private val firebaseMessaging: FirebaseMessaging,
    private val authStateProvider: AuthStateProvider
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val userProfile: UserProfile? = null,
        val username: String = "",
        val friendEmailQuery: String = "",
        val friendUsernameQuery: String = "",
        val searchResults: List<UserProfile> = emptyList(),
        val addFriendStatus: String? = null,
        val friendsList: List<UserProfile> = emptyList(),
        val friendRequestsList: List<UserProfile> = emptyList(),
        val isCheckingUsername: Boolean = false,
        val usernameMessage: String? = null,
        val isUsernameAvailable: Boolean = false
    )

    var uiState by mutableStateOf(UiState(isLoading = true))
        private set

    private val usernameFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            authStateProvider.authState.collectLatest { user ->
                if (user == null) {
                    uiState = UiState(isLoading = false, error = "You are not logged in.")
                }
//                } else {
//                    loadUserProfile(user.uid)
//                }
            }
        }

        viewModelScope.launch {
            usernameFlow
                .debounce(300L)
                .collectLatest { username ->
                    if (username.length < 3) {
                        uiState = uiState.copy(
                            isCheckingUsername = false,
                            usernameMessage = if (username.isNotEmpty()) "Username must be at least 3 characters" else null,
                            isUsernameAvailable = false
                        )
                        return@collectLatest
                    }
                    uiState = uiState.copy(isCheckingUsername = true, usernameMessage = null)
                    val isAvailable = userRepo.isUsernameAvailable(username)
                    uiState = uiState.copy(
                        isCheckingUsername = false,
                        usernameMessage = if (isAvailable) "Username is available!" else "Username is already taken.",
                        isUsernameAvailable = isAvailable
                    )
                }
        }
    }

    private fun loadUserProfile(uid: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                var profile = userRepo.getUserProfile(uid)
                val currentUser = auth.currentUser!!

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

                // Get FCM token and update profile
                val token = firebaseMessaging.token.await()
                if (token.isNotBlank() && profile.fcmToken != token) {
                    profile = profile.copy(fcmToken = token)
                    userRepo.saveUserProfile(profile)
                }

                val finalProfile = profile.copy(
                    uid = if (profile.uid.isBlank()) currentUser.uid else profile.uid,
                    email = if (profile.email.isBlank()) currentUser.email.orEmpty() else profile.email,
                    name = if (profile.name.isBlank()) currentUser.displayName.orEmpty() else profile.name
                )

                val friendsDetails = if (finalProfile.friends.isNotEmpty()) {
                    runCatching { userRepo.getUsers(finalProfile.friends) }.getOrElse { emptyList() }
                } else {
                    emptyList()
                }

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

    fun loadProfileFor(uid: String?) {
        if (uid.isNullOrBlank()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // 🔹 aici NU folosim currentUser, ci UID-ul primit
                val profile = userRepo.getUserProfile(uid)
                    ?: throw Exception("User not found")

                uiState = uiState.copy(
                    isLoading = false,
                    userProfile = profile,
                    username = profile.username,
                    // pe profilul prietenului NU ne interesează lista lui de prieteni / requests
                    friendsList = emptyList(),
                    friendRequestsList = emptyList(),
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load profile."
                )
            }
        }
    }



    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
        usernameFlow.value = value
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

    fun onFriendEmailQueryChange(query: String) {
        uiState = uiState.copy(friendEmailQuery = query)
    }

    fun onFriendUsernameQueryChange(query: String) {
        uiState = uiState.copy(friendUsernameQuery = query)
        viewModelScope.launch {
            if (query.length > 2) {
                val results = userRepo.searchUsersByUsername(query)
                uiState = uiState.copy(searchResults = results, addFriendStatus = null)
            } else {
                uiState = uiState.copy(searchResults = emptyList())
            }
        }
    }

    fun sendFriendRequestByUid(friendUid: String) {
        val currentUser = auth.currentUser ?: return
        if (friendUid == currentUser.uid) {
            uiState = uiState.copy(addFriendStatus = "You cannot add yourself.")
            return
        }

        uiState = uiState.copy(isLoading = true, addFriendStatus = null)
        viewModelScope.launch {
            runCatching {
                userRepo.sendFriendRequest(currentUser.uid, friendUid)
                "Friend request sent!"
            }.onSuccess { msg ->
                uiState = uiState.copy(isLoading = false, addFriendStatus = msg, friendUsernameQuery = "", searchResults = emptyList())
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, addFriendStatus = e.message)
            }
        }
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
                loadUserProfile(currentUser.uid)
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
                loadUserProfile(currentUser.uid)
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
                loadUserProfile(currentUser.uid)
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

    fun deleteAccount() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val user = auth.currentUser
            if (user != null) {
                try {
                    userRepo.deleteUserProfile(user.uid)
                    user.delete().await()
                    uiState = UiState(isLoading = false, error = null, userProfile = null, username = "")
                } catch (e: Exception) {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to delete account."
                    )
                }
            } else {
                uiState = uiState.copy(isLoading = false, error = "You are not logged in.")
            }
        }
    }

    fun resetAllStatsForDev() {
        viewModelScope.launch {
            runCatching {
                userRepo.resetAllUsersStats()
                val currentUser = auth.currentUser?.uid ?: return@runCatching
                loadUserProfile(currentUser) // Reload UI
            }
        }
    }

    fun saveProfile(
        name: String,
        username: String,
        description: String,
        photoUrl: String?
    ) {
        val current = auth.currentUser ?: return
        val currentProfile = uiState.userProfile ?: return

        if (!uiState.isUsernameAvailable && username != currentProfile.username) {
            uiState = uiState.copy(isLoading = false, error = "Username is not available or invalid.")
            return
        }

        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank()) {
            uiState = uiState.copy(isLoading = false, error = "Username cannot be empty")
            return
        }

        val updated = currentProfile.copy(
            name = name.trim(),
            username = trimmedUsername,
            description = description.trim(),
            photoUrl = photoUrl ?: currentProfile.photoUrl
        )

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = runCatching { userRepo.saveUserProfile(updated) }
            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    userProfile = updated,
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

    fun removeProfilePhoto() {
        val current = auth.currentUser ?: return
        val profile = uiState.userProfile ?: return

        if (profile.photoUrl.isBlank()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                userRepo.deleteProfilePhoto(current.uid)
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
        val uid = auth.currentUser?.uid ?: return
        loadUserProfile(uid)
    }
}
