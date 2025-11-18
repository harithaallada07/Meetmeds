package uk.ac.tees.mad.meetmeds.domain.model

sealed class AuthResult {
    data class Success(val uid: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}