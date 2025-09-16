package com.example.casonaapp.homeView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.viewmodels.FontSizeViewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import kotlinx.coroutines.launch

// Datos para los items del menú
data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun HomeView(viewModel: FontSizeViewModel) {
    val fontSize = LocalFontSize.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Items del menú
    val menuItems = listOf(
        MenuItem("home", "Inicio", Icons.Default.Home, "Página principal"),
        MenuItem("profile", "Perfil", Icons.Default.Person, "Mi perfil de usuario"),
        MenuItem("settings", "Configuración", Icons.Default.Settings, "Ajustes de la app"),
        MenuItem("about", "Acerca de", Icons.Default.Info, "Información de la app"),
        MenuItem("logout", "Cerrar sesión", Icons.Default.ExitToApp, "Salir de la aplicación")
    )

    // ModalNavigationDrawer como contenedor principal
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                // Header del drawer
                DrawerHeader()

                // Items del menú
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
                            when (item.id) {
                                "home" -> { /* Navegar a home */ }
                                "profile" -> { /* Navegar a perfil */ }
                                "settings" -> { /* Navegar a configuración */ }
                                "about" -> { /* Navegar a acerca de */ }
                                "logout" -> { /* Cerrar sesión */ }
                            }
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
                    modifier = Modifier.padding(top = 100.dp)
                ){
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
        // Contenido principal
        Scaffold(
            topBar = {
                SimpleTopAppBar(
                    title = "Casona EncantadApp",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                // Para agregar contenido al pie de la pagina a futuro
            }
        ) { innerPadding ->
            // Contenido de las cards
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(5) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Card #${index + 1}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = fontSize
                                )
                            )
                            Text(
                                "Contenido de la card",
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

// Header del Drawer
@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "App Logo",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Casona EncantadApp",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Versión 1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Divider()
}