package com.mindguardians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mindguardians.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel : ViewModel() {

    private val auth       = FirebaseAuth.getInstance()
    private val repository = FirebaseRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Setters de campos ─────────────────────────────────────────────────────
    fun onEmailChange(value: String)           = _uiState.update { it.copy(email = value.trim(), errorMessage = null) }
    fun onPasswordChange(value: String)        = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    fun onDisplayNameChange(value: String)     = _uiState.update { it.copy(displayName = value, errorMessage = null) }

    // ── Limpia el estado (llamar al navegar entre Login <-> Register) ──────────
    fun resetState() {
        _uiState.value = AuthUiState()
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login() {
        val state = _uiState.value
        if (!validateLoginInputs(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.signInWithEmailAndPassword(state.email, state.password).await()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = mapFirebaseError(e.message)) }
            }
        }
    }

    // ── Registro ──────────────────────────────────────────────────────────────
    fun register() {
        val state = _uiState.value
        if (!validateRegisterInputs(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val name = state.displayName.trim().ifBlank { "Guerrero" }

                // 1. Verificar que el nombre de héroe no esté en uso
                val nameTaken = repository.isDisplayNameTaken(name)
                if (nameTaken) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Ese nombre de héroe ya está en uso. Elige otro.")
                    }
                    return@launch
                }

                // 2. Crear usuario en Firebase Auth
                val result = auth.createUserWithEmailAndPassword(state.email, state.password).await()

                // 3. Guardar displayName en el perfil de Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                result.user?.updateProfile(profileUpdates)?.await()

                // 4. Crear documento en Firestore con stats iniciales + email
                repository.createUserProfile(name)

                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = mapFirebaseError(e.message)) }
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState()
    }

    // ── Validaciones ──────────────────────────────────────────────────────────
    private fun validateLoginInputs(state: AuthUiState): Boolean {
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu correo electrónico") }
            return false
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu contraseña") }
            return false
        }
        return true
    }

    private fun validateRegisterInputs(state: AuthUiState): Boolean {
        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu nombre de héroe") }
            return false
        }
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo válido") }
            return false
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return false
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return false
        }
        return true
    }

    // ── Mensajes de error amigables ───────────────────────────────────────────
    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null                               -> "Ocurrió un error inesperado"
            "no user record"     in message              -> "No existe una cuenta con ese correo"
            "password is invalid" in message ||
                    "INVALID_LOGIN_CREDENTIALS" in message       -> "Correo o contraseña incorrectos"
            "email address is already in use" in message -> "Ya existe una cuenta con ese correo"
            "badly formatted"    in message              -> "Formato de correo inválido"
            "network error"      in message              -> "Error de red. Verifica tu conexión"
            "too-many-requests"  in message              -> "Demasiados intentos. Intenta más tarde"
            else                                         -> "Error: $message"
        }
    }
}