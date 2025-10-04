package com.example.casonaapp.userManagment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.data.UserProfile
import com.example.casonaapp.viewmodels.UserViewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserManagementView(
    navController: NavHostController,
    viewModel: UserViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val users by viewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    LaunchedEffect(Unit) {
        println("DEBUG: Users AAAAAA: $users")
    }
    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Gestión de Useros",
                onMenuClick = { navController.popBackStack() },
                customIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("createEditUser")
                }
            ) {
                Icon(Icons.Default.Add, "Crear usero")
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
                "Gestión de Usuarios - Panel Admin",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = fontSize
                )
            )

            users.forEach { user ->
                UserManagementCard(user = user, fontSize = fontSize, viewModel, navController)
            }
        }
    }
}

@Composable
fun UserManagementCard(
    user: UserProfile,
    fontSize: androidx.compose.ui.unit.TextUnit,
    viewModel: UserViewModel = viewModel(),
    navController: NavHostController? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val createdAtText = user.createdAt.let {
        formatter.format(it)
    } ?: "Sin fecha"

    val lastLoginText = user.lastLogin?.let {
        formatter.format(it)
    } ?: "Nunca"

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
                        user.userName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        user.email,
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
                            contentDescription = "Opciones del usero"
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
                                viewModel.setSelectedUser(user)
                                navController?.navigate("createEditUser/${user.uid}")
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

            // Información del usuario
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tipo y fecha creacion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.PersonPin,
                        label = "Tipo de usuario",
                        text = if (user.userType.toString() === ("ADMIN")) "Admin" else "Cliente",
                        fontSize = fontSize
                    )
                    InfoRow(
                        icon = Icons.Default.DateRange,
                        label = "Creado",
                        text = createdAtText,
                        fontSize = fontSize
                    )
                }


                // ultimo login
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoRow(
                        icon = Icons.Default.DateRange,
                        label ="Ultimo inicio sesion: ",
                        text= lastLoginText,
                        fontSize = fontSize
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
                        viewModel.updateUser(user.uid, user.copy(active = !user.active))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (user.active) "Desactivar" else "Activar",
                        fontSize = fontSize * 0.9f
                    )
                }

                // Botón para ver detalles
                Button(
                    onClick = {
                        // Navegar a vista de detalles o previsualización
                        navController?.navigate("userDetails/${user.uid}")
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
                    "Eliminar Usuario",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = fontSize)
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar el usuario \"${user.userName}\"? Esta acción no se puede deshacer.",
                    fontSize = fontSize
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteUser(user.uid)
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
    fontSize: androidx.compose.ui.unit.TextUnit,
    label: String = "",

) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(0.5f)


    ) {
        Column {
            Row {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = fontSize * 1f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

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
}