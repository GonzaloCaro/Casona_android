package com.example.casonaapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {
    // Estado del tema: true = dark, false = light, null = seguir sistema
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = when (_isDarkTheme.value) {
                true -> false // Cambiar de dark a light
                false -> true // Cambiar de light a dark
                null -> true // Si sigue al sistema, cambiar a dark
            }
        }
    }

    fun setDarkTheme(enabled: Boolean?) {
        viewModelScope.launch {
            _isDarkTheme.value = enabled
        }
    }

    fun useSystemTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = null
        }
    }
}