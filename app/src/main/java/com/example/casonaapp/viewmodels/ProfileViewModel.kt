package com.example.casonaapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casonaapp.data.UserProfile
import com.example.casonaapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class ProfileViewModel : ViewModel() {
    private val repository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser
                user?.let { firebaseUser ->
                    var profile = repository.getUserProfile(firebaseUser.uid)

                    if (profile == null) {
                        profile = UserProfile(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            userName = firebaseUser.email?.substringBefore("@") ?: "",
                            displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
                            phoneNumber = firebaseUser.phoneNumber,
                            createdAt = Date(firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()),
                            lastLogin = Date(firebaseUser.metadata?.lastSignInTimestamp ?: System.currentTimeMillis()),
                            bio = "¡Hola! Soy nuevo en Casona EncantadApp."
                        )
                        repository.saveUserProfile(profile)
                    }

                    _userProfile.value = profile
                }
            } catch (e: Exception) {
                _message.value = "Error al cargar perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.updateDisplayName(displayName)
                if (success) {
                    _userProfile.value = _userProfile.value?.copy(displayName = displayName)
                    repository.saveUserProfile(_userProfile.value!!)
                    _message.value = "Nombre actualizado correctamente"
                } else {
                    _message.value = "Error al actualizar nombre"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser
                user?.verifyBeforeUpdateEmail(newEmail)?.await()
                _userProfile.value = _userProfile.value?.copy(email = newEmail)
                repository.saveUserProfile(_userProfile.value!!)
                _message.value = "Se ha enviado un enlace de verificación a tu nuevo correo"
            } catch (e: Exception) {
                _message.value = "Error al actualizar correo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _userProfile.value = _userProfile.value?.copy(phoneNumber = phoneNumber)
                repository.saveUserProfile(_userProfile.value!!)
                _message.value = "Número de teléfono actualizado correctamente"
            } catch (e: Exception) {
                _message.value = "Error al actualizar número: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}