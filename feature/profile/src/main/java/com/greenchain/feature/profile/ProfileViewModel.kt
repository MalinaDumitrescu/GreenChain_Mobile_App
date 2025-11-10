package com.greenchain.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val userProfile: UserProfile? = null
    )

    var uiState by mutableStateOf(UiState(isLoading = true))
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            uiState = UiState(
                isLoading = false,
                error = "You are not logged in."
            )
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {

                    // Mapăm documentul la UserProfile (folosește default-urile din data class)
                    val rawProfile = doc.toObject(UserProfile::class.java)
                    android.util.Log.d("ProfileViewModel", "Loaded photoUrl = ${rawProfile?.photoUrl}")


                    if (rawProfile != null) {
                        // Asigurăm consistența cu auth + doc id
                        val finalProfile = rawProfile.copy(
                            uid = rawProfile.uid.ifBlank { currentUser.uid },
                            email = rawProfile.email.ifBlank { currentUser.email.orEmpty() },
                            // dacă vrei să preiei displayName din Firebase când nu ai name în DB:
                            name = rawProfile.name.ifBlank { currentUser.displayName.orEmpty() }
                        )

                        uiState = UiState(
                            isLoading = false,
                            userProfile = finalProfile,
                            error = null
                        )
                    } else {
                        uiState = UiState(
                            isLoading = false,
                            error = "Failed to parse profile data."
                        )
                    }
                } else {
                    // fallback dacă nu există document: construim profil minimal
                    val fallback = UserProfile(
                        uid = currentUser.uid,
                        email = currentUser.email.orEmpty(),
                        name = currentUser.displayName.orEmpty()
                    )
                    uiState = UiState(
                        isLoading = false,
                        userProfile = fallback,
                        error = null
                    )
                }
            }
            .addOnFailureListener { e ->
                uiState = UiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load profile."
                )
            }
    }

    fun logout() {
        auth.signOut()
        uiState = UiState(
            isLoading = false,
            error = null,
            userProfile = null
        )
    }
}
