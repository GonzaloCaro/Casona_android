package com.example.casonaapp.eventManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.DatePickerDialog
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.data.Event
import com.example.casonaapp.viewmodels.EventViewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateEditEventView(
    navController: NavHostController,
    eventId: String? = null, // Si es null, es crear; si tiene valor, es editar
    viewModel: EventViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var availableTickets by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        println("DEBUG: CreateEditEventView - eventId: $eventId")
        println("DEBUG: Events disponibles: ${events.size}")
    }

    LaunchedEffect(eventId, events) {
        if (eventId != null && eventId.isNotBlank()) {
            println("DEBUG: Buscando evento con ID: $eventId")
            val eventToEdit = events.find { it.id == eventId }
            if (eventToEdit != null) {
                println("DEBUG: Evento encontrado: ${eventToEdit.title}")
                viewModel.setSelectedEvent(eventToEdit)
            } else {
                println("DEBUG: Evento NO encontrado en la lista")
            }
        } else {
            println("DEBUG: Modo creación - limpiando formulario")
            // Limpiar formulario para creación
            title = ""
            description = ""
            location = ""
            price = ""
            availableTickets = ""
            selectedDate = System.currentTimeMillis()
            viewModel.clearSelectedEvent()
        }
    }

    LaunchedEffect(selectedEvent) {
        if (selectedEvent != null) {
            println("DEBUG: Cargando datos del selectedEvent: ${selectedEvent?.title}")
            title = selectedEvent?.title ?: ""
            description = selectedEvent?.description ?: ""
            location = selectedEvent?.location ?: ""
            price = selectedEvent?.price?.toString() ?: ""
            availableTickets = selectedEvent?.availableTickets?.toString() ?: ""
            selectedDate = selectedEvent?.date?.time ?: System.currentTimeMillis()
        }
    }

    LaunchedEffect(eventId) {
        if (eventId != null && eventId.isNotBlank() && events.isEmpty()) {
            println("DEBUG: Recargando eventos para encontrar: $eventId")
            viewModel.loadEvents()
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
            viewModel.resetOperationSuccess()
        }
    }

    // Cargar datos del evento al editar
    LaunchedEffect(selectedEvent) {
        if (eventId != null && selectedEvent != null) {
            title = selectedEvent?.title ?: ""
            description = selectedEvent?.description ?: ""
            location = selectedEvent?.location ?: ""
            price = selectedEvent?.price.toString()
            availableTickets = selectedEvent?.availableTickets.toString()
            selectedDate = selectedEvent?.date?.time ?: System.currentTimeMillis()
        }
    }

    // Si es edición, cargar el evento
    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.events.value.find { it.id == eventId }?.let {
                viewModel.setSelectedEvent(it)
            }
        } else {
            // Si es creación, resetear el formulario
            title = ""
            description = ""
            location = ""
            price = ""
            availableTickets = ""
            selectedDate = System.currentTimeMillis()
        }
    }

    DatePickerDialog (
        showDialog = showDatePicker,
        onDismiss = { showDatePicker = false },
        onDateSelected = { newDate ->
            selectedDate = newDate
        },
        initialDate = selectedDate
    )

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (eventId == null) "Crear Evento" else "Editar Evento",
                onMenuClick = { navController.popBackStack() },
                customIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                if (eventId == null) "Crear Nuevo Evento" else "Editar Evento",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize)
            )

            // Campo Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del evento") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank()
            )

            // Campo Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                isError = description.isBlank()
            )

            // Campo Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = location.isBlank()
            )

            // Selector de Fecha
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (showDatePicker) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Fecha del evento:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                        )
                        Text(
                            SimpleDateFormat("EEEE, dd MMMM yyyy 'a las' HH:mm", Locale.getDefault()).format(Date(selectedDate)),
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize)
                        )
                    }
                    Icon(Icons.Default.DateRange, "Seleccionar fecha")
                }
            }

            // Campos de precio y tickets en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo Precio
                OutlinedTextField(
                    value = price,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            price = newValue
                        }
                    },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    prefix = { Text("$") },
                    isError = price.isBlank() || price.toDoubleOrNull() == null
                )

                // Campo Tickets disponibles
                OutlinedTextField(
                    value = availableTickets,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                            availableTickets = newValue
                        }
                    },
                    label = { Text("Tickets") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = availableTickets.isBlank() || availableTickets.toIntOrNull() == null
                )
            }

            // Mostrar mensajes
            if (message != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (operationSuccess) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            message ?: "",
                            modifier = Modifier.weight(1f),
                            color = if (operationSuccess) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        if (operationSuccess) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            // Botón de guardar
            Button(
                onClick = {
                    if (validateForm(title, description, location, price, availableTickets)) {
                        val event = Event(
                            id = eventId ?: "",
                            title = title,
                            description = description,
                            location = location,
                            price = price.toDouble(),
                            availableTickets = availableTickets.toInt(),
                            date = Date(selectedDate),
                            createdBy = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        )

                        if (eventId == null) {
                            viewModel.createEvent(event)
                        } else {
                            viewModel.updateEvent(eventId, event)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && validateForm(title, description, location, price, availableTickets)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (eventId == null) "Crear Evento" else "Actualizar Evento")
                }
            }

            // Botón de cancelar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

// Función de validación del formulario
private fun validateForm(
    title: String,
    description: String,
    location: String,
    price: String,
    availableTickets: String
): Boolean {
    return title.isNotBlank() &&
            description.isNotBlank() &&
            location.isNotBlank() &&
            price.isNotBlank() &&
            price.toDoubleOrNull() != null &&
            availableTickets.isNotBlank() &&
            availableTickets.toIntOrNull() != null
}