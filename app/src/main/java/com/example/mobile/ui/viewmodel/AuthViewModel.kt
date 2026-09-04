package com.example.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val signedInName: String = " ",
    val loginSucceeded: Boolean = false,
    val signUpSucceeded: Boolean = false,
    val logoutSucceeded: Boolean = false,

    val passwordResetSucceeded: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val supabase = SupabaseProvider.client

    private val _auState = MutableStateFlow(AuthUiState())
    val auState: StateFlow<AuthUiState> = _auState.asStateFlow()

    fun onFullNameChange(value: String) {
        _auState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _auState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _auState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _auState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _auState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleRememberMe() {
        _auState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun isLoginFormValid(): Boolean {
        val state = _auState.value
        return isValidEmail(state.email) && state.password.length >= 6
    }

    fun isSignUpFormValid(): Boolean {
        val state = _auState.value
        return state.fullName.isNotBlank() &&
                isValidEmail(state.email) &&
                state.password.length >= 6 &&
                state.password == state.confirmPassword
    }

    fun isPasswordResetFormValid(): Boolean {
        val state = _auState.value
        return state.password.length >= 6 && state.password == state.confirmPassword
    }

    fun signIn() {
        val state = _auState.value
        if (!isLoginFormValid()) {
            _auState.update {
                it.copy(errorMessage = "Enter a valid email and a password of at least 6 characters.")
            }
            return
        }

        _auState.update {
            it.copy(isLoading = true, errorMessage = null, infoMessage = null)
        }

        viewModelScope.launch {
            try {
                supabase.auth.signInWith(Email) {
                    email = state.email.trim()
                    password = state.password
                }

                val fallbackName = state.email
                    .substringBefore("@")
                    .replace('.', ' ')
                    .replace('_', ' ')
                    .split(' ')
                    .filter { it.isNotBlank() }
                    .joinToString(" ") { part ->
                        part.replaceFirstChar { character -> character.uppercase() }
                    }
                    .ifBlank { "Eco User" }

                _auState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        signedInName = fallbackName,
                        loginSucceeded = true
                    )
                }
            } catch (error: Exception) {
                _auState.update {
                    it.copy(isLoading = false, errorMessage = friendlyAuthError(error))
                }
            }
        }
    }

    fun signUp() {
        val state = _auState.value
        if (!isSignUpFormValid()) {
            _auState.update {
                it.copy(errorMessage = "Complete every field and make sure the passwords match.")
            }
            return
        }

        _auState.update {
            it.copy(isLoading = true, errorMessage = null, infoMessage = null)
        }

        viewModelScope.launch {
            try {
                supabase.auth.signUpWith(Email) {
                    email = state.email.trim()
                    password = state.password
                    data = buildJsonObject {
                        put("full_name", state.fullName.trim())
                    }
                }

                _auState.update {
                    it.copy(
                        fullName = "",
                        password = "",
                        confirmPassword = "",
                        isLoading = false,
                        errorMessage = null,
                        infoMessage = "Account created. Confirm your email if required, then sign in.",
                        signUpSucceeded = true
                    )
                }
            } catch (error: Exception) {
                _auState.update {
                    it.copy(isLoading = false, errorMessage = friendlyAuthError(error))
                }
            }
        }
    }

    fun signOut() {
        _auState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
            } catch (_: Exception) {
                // The user must still be able to leave the local app session offline.
            } finally {
                _auState.value = AuthUiState(logoutSucceeded = true)
            }
        }
    }

    fun resetPassword() {
        val email = _auState.value.email.trim()

        if (!isValidEmail(email)) {
            _auState.update {
                it.copy(errorMessage = "Email is not valid !", infoMessage = null)
            }
            return
        }
        _auState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                infoMessage = null
            )
        }

        viewModelScope.launch {
            try {
                supabase.auth.resetPasswordForEmail(
                    email = email,
                    redirectUrl = "ecopulse://reset-password"
                )
                _auState.update {
                    it.copy(
                        infoMessage = " password reset link has been sent to your email. ",
                        isLoading = false
                    )
                }
            } catch (error: Exception) {
                _auState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = friendlyAuthError(error)
                    )
                }
            }
        }
    }

    fun updatePassword() {
        val state = _auState.value

        if (state.password.length < 6) {
            _auState.update {
                it.copy(
                    errorMessage = "Password must contain at least 6 characters.",
                    infoMessage = null
                )
            }
            return
        }
        if (state.password != state.confirmPassword) {
            _auState.update {
                it.copy(
                    errorMessage = "Passwords do not match.",
                    infoMessage = null
                )
            }
            return
        }

        _auState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                infoMessage = null
            )
        }

        viewModelScope.launch {

            try {
                supabase.auth.updateUser {
                    password = state.password
                }

                _auState.update {
                    it.copy(
                        password = "",
                        confirmPassword = "",
                        isLoading = false,
                        errorMessage = null,
                        infoMessage =
                            "Password updated successfully. You can now sign in.",
                        passwordResetSucceeded = true
                    )
                }

            } catch (error: Exception) {

                _auState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = friendlyAuthError(error)
                    )
                }
            }
        }
    }

    fun preparePasswordReset() {
        _auState.update {
            it.copy(
                password = "",
                confirmPassword = "",
                errorMessage = null,
                infoMessage = null,
                passwordResetSucceeded = false
            )
        }
    }

    fun consumePasswordResetSuccess() {
        _auState.update {
            it.copy(passwordResetSucceeded = false)
        }
    }

    fun consumeLoginSuccess() {
        _auState.update { it.copy(loginSucceeded = false, password = "") }
    }

    fun consumeSignUpSuccess() {
        _auState.update { it.copy(signUpSucceeded = false) }
    }

    fun consumeLogoutSuccess() {
        _auState.update { it.copy(logoutSucceeded = false) }
    }

    private fun isValidEmail(value: String): Boolean =
        EMAIL_PATTERN.matches(value.trim())

    private fun friendlyAuthError(error: Exception): String {
        val message = error.message.orEmpty()
        return when {
            message.contains("Invalid login credentials", ignoreCase = true) ->
                "Incorrect email or password."

            message.contains("Email not confirmed", ignoreCase = true) ->
                "Confirm your email before signing in."

            message.contains("User already registered", ignoreCase = true) ->
                "An account with this email already exists."

            message.isNotBlank() -> message
            else -> "Authentication failed. Check your internet connection and try again."
        }
    }

    companion object {
        private val EMAIL_PATTERN =
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    }
}
