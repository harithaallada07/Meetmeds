package uk.ac.tees.mad.meetmeds.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.meetmeds.domain.model.AuthResult
import uk.ac.tees.mad.meetmeds.domain.repository.AuthRepository
import uk.ac.tees.mad.meetmeds.presentation.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // State to control Splash Screen visibility
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    // State to determine where to navigate after Splash
    private val _startDestination = mutableStateOf(Screen.Auth.route)
    val startDestination: State<String> = _startDestination

    // UI States for the Auth Screen forms
    private val _authState = mutableStateOf<AuthResult?>(null)
    val authState: State<AuthResult?> = _authState

    // State specifically for Password Reset to avoid conflicting with Login Navigation
    private val _resetPasswordState = mutableStateOf<AuthResult?>(null)
    val resetPasswordState: State<AuthResult?> = _resetPasswordState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        authRepository.getCurrentUser().onEach { isLoggedIn ->
            if (isLoggedIn) {
                // If logged in -> go to Medicine List
                _startDestination.value = Screen.Main.route
            } else {
                // If not logged in -> show login/signup
                _startDestination.value = Screen.Auth.route
            }
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

    // Function to trigger password reset
    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).collect { result ->
                _resetPasswordState.value = result
            }
        }
    }

    // Helper to reset the state after showing the toast
    fun clearResetPasswordState() {
        _resetPasswordState.value = null
    }
}