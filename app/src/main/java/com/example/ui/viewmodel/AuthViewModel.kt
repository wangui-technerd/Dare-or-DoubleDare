package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DareRepository
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserEntity? = null,
    val isGuest: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAuthenticating: Boolean = false
)

class AuthViewModel(private val repository: DareRepository) : ViewModel() {

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Auto sign-in to the first user if available, or create a default host if clean
        viewModelScope.launch {
            repository.allUsers.collect { users ->
                if (_uiState.value.currentUser == null && users.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(currentUser = users.first())
                }
            }
        }
    }

    fun register(
        username: String,
        displayName: String,
        avatarEmoji: String,
        avatarColorHex: String,
        safeWord: String,
        intensity: Int
    ) {
        viewModelScope.launch {
            if (username.isBlank() || displayName.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all required fields.")
                return@launch
            }
            _uiState.value = _uiState.value.copy(isAuthenticating = true, errorMessage = null)
            val newUser = UserEntity(
                username = username.trim().lowercase(),
                displayName = displayName.trim(),
                avatarEmoji = avatarEmoji.ifBlank { "💋" },
                avatarColorHex = avatarColorHex,
                safeWord = safeWord.ifBlank { "Pineapple" },
                intensityPreference = intensity.coerceIn(1, 4)
            )
            val result = repository.registerUser(newUser)
            result.onSuccess { id ->
                _uiState.value = _uiState.value.copy(
                    currentUser = newUser.copy(id = id),
                    isGuest = false,
                    isAuthenticating = false,
                    successMessage = "Profile created successfully!"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    errorMessage = err.message ?: "Failed to create profile"
                )
            }
        }
    }

    fun selectUser(user: UserEntity) {
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            isGuest = false,
            errorMessage = null,
            successMessage = "Switched to ${user.displayName}"
        )
    }

    fun continueAsGuest(guestName: String = "Guest Host") {
        _uiState.value = _uiState.value.copy(
            currentUser = UserEntity(
                id = -1,
                username = "guest",
                displayName = guestName,
                avatarEmoji = "✨",
                avatarColorHex = "#C92A45",
                safeWord = "Pineapple"
            ),
            isGuest = true,
            errorMessage = null
        )
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            currentUser = null,
            isGuest = false,
            errorMessage = null,
            successMessage = null
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context.applicationContext)
            val repo = DareRepository(db)
            return AuthViewModel(repo) as T
        }
    }
}
