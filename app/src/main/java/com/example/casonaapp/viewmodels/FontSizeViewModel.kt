package com.example.casonaapp.viewmodels

import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FontSizeViewModel : ViewModel() {
    private val _fontSize = MutableStateFlow(16.sp)
    val fontSize = _fontSize.asStateFlow()

    fun increaseFontSize() {
        if (_fontSize.value.value < 30f) { // Límite máximo
            _fontSize.value = (_fontSize.value.value + 2f).sp
        }
    }

    fun decreaseFontSize() {
        if (_fontSize.value.value > 16f) { // Límite mínimo
            _fontSize.value = (_fontSize.value.value - 2f).sp
        }
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size.sp
    }
}