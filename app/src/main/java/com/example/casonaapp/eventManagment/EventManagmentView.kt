package com.example.casonaapp.eventManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.data.Event
import com.example.casonaapp.viewmodels.EventViewModel
import com.example.casonaapp.viewmodels.LocalFontSize

@Composable
fun EventManagementView(
    navController: NavHostController,
    viewModel: EventViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val events by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Gestión de Eventos",
                onMenuClick = { navController.popBackStack() },
                customIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("createEditEvent")
                }
            ) {
                Icon(Icons.Default.Add, "Crear evento")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Gestión de Eventos - Panel Admin",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = fontSize
                )
            )

            events.forEach { event ->
                EventManagementCard(event = event, fontSize = fontSize)
            }
        }
    }
}

@Composable
fun EventManagementCard(
    event: Event,
    fontSize: androidx.compose.ui.unit.TextUnit,
    viewModel: EventViewModel = viewModel(),
    navController: NavHostController? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con título y menú de opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = fontSize,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        event.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = fontSize * 0.9f
                        ),
                        maxLines = 2
                    )
                }

                // Menú de opciones (tres puntos)
                Box {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones del evento"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Editar",
                                    fontSize = fontSize * 0.9f
                                )
                            },
                            onClick = {
                                showMenu = false
                                viewModel.setSelectedEvent(event)
                                navController?.navigate("createEditEvent/${event.id}") {
                                    launchSingleTop = true
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, null)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Eliminar",
                                    fontSize = fontSize * 0.9f
                                )
                            },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del evento
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fecha y ubicación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.DateRange,
                        text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(event.date),
                        fontSize = fontSize
                    )
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        text = event.location,
                        fontSize = fontSize
                    )
                }

                // Precio y tickets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.AttachMoney,
                        text = "$${event.price}",
                        fontSize = fontSize
                    )
                    InfoRow(
                        icon = Icons.Default.ConfirmationNumber,
                        text = "${event.availableTickets} tickets",
                        fontSize = fontSize
                    )
                }

                // Estado y creador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estado del evento
                    Text(
                        if (event.active) "Activo" else "Inactivo",
                        fontSize = fontSize * 0.9f
                    )
                    // Fecha de creación
                    Text(
                        "Creado: ${java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault()).format(event.createdAt)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = fontSize * 0.8f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Acciones rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para desactivar/activar
                OutlinedButton(
                    onClick = {
                        viewModel.updateEvent(event.id, event.copy(active = !event.active))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (event.active) "Desactivar" else "Activar",
                        fontSize = fontSize * 0.9f
                    )
                }

                // Botón para ver detalles
                Button(
                    onClick = {
                        // Navegar a vista de detalles o previsualización
                        navController?.navigate("eventDetails/${event.id}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver Detalles", fontSize = fontSize * 0.9f)
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar Evento",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = fontSize)
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar el evento \"${event.title}\"? Esta acción no se puede deshacer.",
                    fontSize = fontSize
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEvent(event.id)
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = fontSize
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar", fontSize = fontSize)
                }
            }
        )
    }
}

// Componente auxiliar para mostrar información con icono
@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = fontSize * 0.9f,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}