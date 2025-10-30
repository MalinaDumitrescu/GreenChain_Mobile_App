package com.greenchain.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.core.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui

    fun onEmail(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun onPassword(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun login() = authAction { repo.login(_ui.value.email.trim(), _ui.value.password) }
    fun register() = authAction { repo.register(_ui.value.email.trim(), _ui.value.password) }

    private fun authAction(block: suspend () -> Unit) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                block()
                _ui.value = _ui.value.copy(loading = false, success = true)
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(loading = false, error = t.message ?: "Auth failed")
            }
        }
    }
}
