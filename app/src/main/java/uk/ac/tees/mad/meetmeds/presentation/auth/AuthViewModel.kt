package uk.ac.tees.mad.meetmeds.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.meetmeds.domain.model.AuthResult
import uk.ac.tees.mad.meetmeds.domain.repository.AuthRepository
import uk.ac.tees.mad.meetmeds.presentation.navigation.Screen

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination = mutableStateOf(Screen.Auth.route)
    val startDestination: State<String> = _startDestination

    private val _authState = mutableStateOf<AuthResult?>(null)
    val authState: State<AuthResult?> = _authState

    private val _resetPasswordState = mutableStateOf<AuthResult?>(null)
    val resetPasswordState: State<AuthResult?> = _resetPasswordState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        authRepository.getCurrentUser().onEach { isLoggedIn ->
            _startDestination.value = if (isLoggedIn) Screen.Main.route else Screen.Auth.route
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    fun loginUser(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.loginUser(email, pass).collect { result ->
                _authState.value = result
            }
        }
    }

    fun registerUser(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.registerUser(email, pass).collect { result ->
                _authState.value = result
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).collect { result ->
                _resetPasswordState.value = result
            }
        }
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = null
    }

    // Factory for manual ViewModel creation
    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}