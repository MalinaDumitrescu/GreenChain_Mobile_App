package com.greenchain.feature.scan.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.scan.data.ScanRepository
import com.greenchain.feature.profile.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log


@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repo: ScanRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying

    private val _isValid = MutableStateFlow<Boolean?>(null)
    val isValid: StateFlow<Boolean?> = _isValid
    fun verifyCropped(cropped: Bitmap) {
        Log.d("ScanVM", "verifyCropped() called, size=${cropped.width}x${cropped.height}")

        _isVerifying.value = true
        _isValid.value = null

        viewModelScope.launch {
            val ok = runCatching { repo.verifySgrLogo(cropped) }
                .onFailure { e ->
                    Log.e("ScanVM", "Error in verifySgrLogo", e)
                }
                .getOrElse { false }

            if (ok) {
                addBottleToCurrentUser()
                addPointsToCurrentUser(5)
            }

            Log.d("ScanVM", "verifyCropped() result = $ok")
            _isValid.value = ok
            _isVerifying.value = false
        }
    }

    private suspend fun addPointsToCurrentUser(amount: Int) {
        val uid = auth.currentUser?.uid ?: return
        val userProfile = userRepository.getUserProfile(uid)

        userProfile?.let {
            val updatedProfile = it.copy(
                points = it.points + amount
            )
            userRepository.saveUserProfile(updatedProfile)
        }
    }

    private suspend fun addBottleToCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        val userProfile = userRepository.getUserProfile(uid)

        userProfile?.let {
            val updatedProfile = it.copy(
                bottleCount = it.bottleCount + 1
            )
            userRepository.saveUserProfile(updatedProfile)
        }
    }

    fun simulateSuccessfulScan() {
        viewModelScope.launch {
            addPointsToCurrentUser(1)
        }
    }

    fun reset() {
        _isValid.value = null
    }
}
