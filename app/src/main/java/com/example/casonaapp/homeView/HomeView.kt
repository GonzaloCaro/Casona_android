package com.example.casonaapp.homeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.data.Event
import com.example.casonaapp.data.UserType
import com.example.casonaapp.viewmodels.EventViewModel
import com.example.casonaapp.viewmodels.FontSizeViewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import com.example.casonaapp.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

// Datos para los items del menú
data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val requiresAdmin: Boolean = false
)

@Composable
fun HomeView(
    viewModel: FontSizeViewModel,
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userProfile by profileViewModel.userProfile.collectAsState()
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    // Cargar eventos al iniciar
    LaunchedEffect(Unit) {
        eventViewModel.loadEvents()
    }

    // Items del menú base
    val baseMenuItems = listOf(
        MenuItem("home", "Inicio", Icons.Default.Home, "Página principal"),
        MenuItem("profile", "Perfil", Icons.Default.Person, "Mi perfil de usuario"),
        MenuItem("events", "Gestión de Eventos", Icons.Default.Event, "Administrar eventos", requiresAdmin = true),
        MenuItem("settings", "Configuración", Icons.Default.Settings, "Ajustes de la app"),
        MenuItem("about", "Acerca de", Icons.Default.Info, "Información de la app"),
        MenuItem("logout", "Cerrar sesión", Icons.AutoMirrored.Filled.ExitToApp, "Salir de la aplicación")
    )

    // Filtrar items según el tipo de usuario
    val menuItems = baseMenuItems.filter { item ->
        !item.requiresAdmin || userProfile?.userType == UserType.ADMIN
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerHeader(userProfile)

                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.title,
                                fontSize = fontSize
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            handleMenuItemClick(item.id, navController)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.description
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.decreaseFontSize() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("A-", fontSize = fontSize)
                    }
                    Button(
                        onClick = { viewModel.increaseFontSize() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("A+", fontSize = fontSize)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SimpleTopAppBar(
                    title = "Casona EncantadApp",
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { innerPadding ->
            if (isLoading && events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mostrar eventos
                    events.forEach { event ->
                        EventCard(
                            event = event,
                            fontSize = fontSize,
                            userProfile = userProfile,
                            onEdit = {
                                eventViewModel.setSelectedEvent(event)
                                navController.navigate("createEditEvent/${event.id}")
                            },
                            onDelete = {
                                // Mostrar diálogo de confirmación
                                eventViewModel.deleteEvent(event.id)
                            },
                            onView = {
                                // Navegar a vista detallada del evento
                                navController.navigate("eventDetails/${event.id}")
                            }
                        )
                    }

                    // Mensaje si no hay eventos
                    if (events.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No hay eventos disponibles",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = fontSize
                                    )
                                )
                                Text(
                                    "Próximamente se anunciarán nuevos eventos",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = fontSize
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función para manejar clicks en el menú
private fun handleMenuItemClick(itemId: String, navController: NavHostController) {
    when (itemId) {
        "home" -> navController.navigate("home")
        "profile" -> navController.navigate("profile")
        "events" -> navController.navigate("eventManagement") // Nueva ruta
        "settings" -> { /* Navegar a configuración */ }
        "about" -> { /* Navegar a acerca de */ }
        "logout" -> {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}

// Componente para mostrar eventos
@Composable
fun EventCard(
    event: Event,
    fontSize: androidx.compose.ui.unit.TextUnit,
    userProfile: com.example.casonaapp.data.UserProfile?,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onView: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con título y menú (solo para admin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = fontSize,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Menú de opciones solo para admin
                if (userProfile?.userType == UserType.ADMIN) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones"
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, null)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                event.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = fontSize
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información del evento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "📅 ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(event.date)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = fontSize * 0.9f
                        )
                    )
                    Text(
                        "📍 ${event.location}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = fontSize * 0.9f
                        )
                    )
                }

                Text(
                    "$${event.price}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = fontSize,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer con tickets y botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${event.availableTickets} tickets disponibles",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = fontSize * 0.9f,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Botón de ver evento (para clientes) o estado (para admin)
                if (userProfile?.userType == UserType.ADMIN) {
                    Text(
                        "Admin",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = fontSize * 0.8f,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                } else {
                    Button(
                        onClick = onView,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Ver Evento")
                    }
                }
            }
        }
    }
}

// Header del Drawer
@Composable
fun DrawerHeader(userProfile: com.example.casonaapp.data.UserProfile?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "App Logo",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            userProfile?.displayName ?: "Usuario",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            userProfile?.userType?.name ?: "CLIENT",
            style = MaterialTheme.typography.bodySmall,
            color = if (userProfile?.userType == UserType.ADMIN) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            "Versión 1.0.2",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
}