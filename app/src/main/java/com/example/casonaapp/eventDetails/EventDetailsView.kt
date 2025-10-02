package com.example.casonaapp.eventDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.viewmodels.EventViewModel
import com.example.casonaapp.viewmodels.LocalFontSize

@Composable
fun EventDetailsView(
    navController: NavHostController,
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val events by viewModel.events.collectAsState()

    // Buscar el evento por ID
    val event = events.find { it.id == eventId }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Detalles del Evento",
                onMenuClick = { navController.popBackStack() },
                customIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        },
        bottomBar = {
            if (event != null) {
                Surface(
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "$${event.price}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = fontSize,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                            Text(
                                "${event.availableTickets} tickets disponibles",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = fontSize)
                            )
                        }
                        Button(
                            onClick = { /* Comprar ticket */ }
                        ) {
                            Text("Comprar Ticket")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Work in progres...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Contenido detallado del evento
                // ... (similar al EventCard pero más detallado)
            }
        }
    }
}