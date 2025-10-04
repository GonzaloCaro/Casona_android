package com.example.casonaapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casonaapp.data.UserProfile
import com.example.casonaapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel(){
    
    private val repository = UserRepository()
    
    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _selectedUser = MutableStateFlow<UserProfile?>(null)
    val selectedUser = _selectedUser.asStateFlow()

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess = _operationSuccess.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedUsers = repository.getAllUsers()
                _users.value = loadedUsers
            } catch (e: Exception) {
                _message.value = "Error al cargar usersos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedUsers = repository.getAllUsers()
                _users.value = loadedUsers
            } catch (e: Exception) {
                _message.value = "Error al cargar useros activos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createUser(user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val userId = repository.createUser(user)
                if (userId != null) {
                    _message.value = "Usero creado exitosamente"
                    _operationSuccess.value = true
                    loadUsers() // Recargar la lista
                } else {
                    _message.value = "Error al crear usero"
                    _operationSuccess.value = false
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedUser(user: UserProfile) {
        _selectedUser.value = user
    }

    // Agrega esta función para limpiar la selección
    fun clearSelectedUser() {
        _selectedUser.value = null
    }

    fun updateUser(userId: String, user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val success = repository.updateUser(userId, user)
                if (success) {
                    _message.value = "Usero actualizado exitosamente"
                    _operationSuccess.value = true
                    loadUsers() // Recargar la lista
                } else {
                    _message.value = "Error al actualizar usero"
                    _operationSuccess.value = false
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val success = repository.deleteUser(userId)
                if (success) {
                    _message.value = "Usero eliminado exitosamente"
                    _operationSuccess.value = true
                    loadUsers() // Recargar la lista
                } else {
                    _message.value = "Error al eliminar usero"
                    _operationSuccess.value = false
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun resetOperationSuccess() {
        _operationSuccess.value = false
    }
    
}