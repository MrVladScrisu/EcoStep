package com.example.ecostep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecostep.data.model.User
import com.example.ecostep.data.remote.FirebaseRepository
import com.example.ecostep.data.remote.FirebaseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(
    private val firebaseRepository: FirebaseRepository = FirebaseRepositoryImpl()
) : ViewModel() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = firebaseRepository.getCurrentUser()
        }
    }
    
    /**
     * Login cu QR code
     */
    fun loginWithQrCode(qrCode: String, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val user = firebaseRepository.getUserByQrCode(qrCode)
                if (user != null) {
                    // Autentificăm user-ul în Firebase Auth (anonym sau custom token)
                    // Pentru simplitate, folosim anonymous auth
                    FirebaseAuth.getInstance().signInAnonymously()
                        .addOnSuccessListener {
                            _currentUser.value = user
                            _isLoading.value = false
                            onSuccess(user)
                        }
                        .addOnFailureListener { e ->
                            _error.value = "Eroare la autentificare: ${e.message}"
                            _isLoading.value = false
                            onError(e.message ?: "Eroare necunoscută")
                        }
                } else {
                    _error.value = "Cod QR invalid sau user inexistent"
                    _isLoading.value = false
                    onError("Cod QR invalid")
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
                onError(e.message ?: "Eroare necunoscută")
            }
        }
    }
    
    /**
     * Creează un user nou și generează QR code
     */
    fun createUser(email: String, displayName: String, onSuccess: (User, String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Generăm un ID unic și QR code
                val userId = UUID.randomUUID().toString()
                val qrCode = UUID.randomUUID().toString()
                
                val user = User(
                    id = userId,
                    email = email,
                    displayName = displayName,
                    qrCode = qrCode
                )
                
                val result = firebaseRepository.createUser(user)
                result.fold(
                    onSuccess = {
                        // Autentificăm user-ul
                        FirebaseAuth.getInstance().signInAnonymously()
                            .addOnSuccessListener {
                                _currentUser.value = user
                                _isLoading.value = false
                                onSuccess(user, qrCode)
                            }
                            .addOnFailureListener { e ->
                                _error.value = "Eroare la autentificare: ${e.message}"
                                _isLoading.value = false
                                onError(e.message ?: "Eroare necunoscută")
                            }
                    },
                    onFailure = { e ->
                        _error.value = e.message
                        _isLoading.value = false
                        onError(e.message ?: "Eroare necunoscută")
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
                onError(e.message ?: "Eroare necunoscută")
            }
        }
    }
    
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
    }
}

