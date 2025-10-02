package com.example.casonaapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casonaapp.data.Event
import com.example.casonaapp.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess = _operationSuccess.asStateFlow()

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedEvents = repository.getEvents()
                _events.value = loadedEvents
            } catch (e: Exception) {
                _message.value = "Error al cargar eventos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createEvent(event: Event) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val eventId = repository.createEvent(event)
                if (eventId != null) {
                    _message.value = "Evento creado exitosamente"
                    _operationSuccess.value = true
                    loadEvents() // Recargar la lista
                } else {
                    _message.value = "Error al crear evento"
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

    fun setSelectedEvent(event: Event) {
        _selectedEvent.value = event
    }

    // Agrega esta función para limpiar la selección
    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun updateEvent(eventId: String, event: Event) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val success = repository.updateEvent(eventId, event)
                if (success) {
                    _message.value = "Evento actualizado exitosamente"
                    _operationSuccess.value = true
                    loadEvents() // Recargar la lista
                } else {
                    _message.value = "Error al actualizar evento"
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

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = false
            try {
                val success = repository.deleteEvent(eventId)
                if (success) {
                    _message.value = "Evento eliminado exitosamente"
                    _operationSuccess.value = true
                    loadEvents() // Recargar la lista
                } else {
                    _message.value = "Error al eliminar evento"
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

    fun loadEventById(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val event = repository.getEventById(eventId)
                if (event != null) {
                    _selectedEvent.value = event
                } else {
                    _message.value = "Evento no encontrado"
                }
            } catch (e: Exception) {
                _message.value = "Error al cargar evento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetOperationSuccess() {
        _operationSuccess.value = false
    }
}