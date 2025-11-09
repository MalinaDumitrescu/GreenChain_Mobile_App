package com.greenchain.feature.scan.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.scan.data.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repo: ScanRepository
) : ViewModel() {

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying

    private val _isValid = MutableStateFlow<Boolean?>(null)
    val isValid: StateFlow<Boolean?> = _isValid

    fun verifyCropped(cropped: Bitmap) {
        _isVerifying.value = true
        _isValid.value = null

        viewModelScope.launch {
            val ok = runCatching { repo.verifySgrLogo(cropped) }
                .getOrElse { false }

            _isValid.value = ok
            _isVerifying.value = false
        }
    }

    fun reset() {
        _isValid.value = null
    }
}
