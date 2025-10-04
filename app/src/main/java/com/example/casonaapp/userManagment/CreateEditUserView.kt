package com.example.casonaapp.userManagment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.data.UserProfile
import com.example.casonaapp.data.UserType
import com.example.casonaapp.viewmodels.LocalFontSize
import com.example.casonaapp.viewmodels.UserViewModel

@Composable
fun CreateEditUserView(
    navController: NavHostController,
    userId: String? = null, // Si es null, es crear; si tiene valor, es editar
    viewModel: UserViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val selectedUser by viewModel.selectedUser.collectAsState()
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    // Estados del formulario
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var userType: UserType? by remember { mutableStateOf(UserType.CLIENT) }
    var active by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        println("DEBUG: CreateEditUserView - UserId: $userId")
        println("DEBUG: Users disponibles: ${users.size}")
    }

    LaunchedEffect(userId, users) {
        if (userId != null && userId.isNotBlank()) {
            println("DEBUG: Buscando usero con ID: $userId")
            val userToEdit = users.find { it.uid == userId }
            if (userToEdit != null) {
                println("DEBUG: Usero encontrado: ${userToEdit.displayName}")
                viewModel.setSelectedUser(userToEdit)
            } else {
                println("DEBUG: Usero NO encontrado en la lista")
            }
        } else {
            println("DEBUG: Modo creación - limpiando formulario")
            // Limpiar formulario para creación
            email = ""
            displayName = ""
            phoneNumber = ""
            userType = UserType.CLIENT
            viewModel.clearSelectedUser()
        }
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            println("DEBUG: Cargando datos del selectedUser: ${selectedUser?.displayName}")
            email = selectedUser?.email ?: ""
            displayName = selectedUser?.displayName ?: ""
            phoneNumber = selectedUser?.phoneNumber ?: ""
            userType = selectedUser?.userType
            active = selectedUser?.active ?: true
        }
    }

    LaunchedEffect(userId) {
        if (userId != null && userId.isNotBlank() && users.isEmpty()) {
            println("DEBUG: Recargando useros para encontrar: $userId")
            viewModel.loadUsers()
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
            viewModel.resetOperationSuccess()
        }
    }

    // Cargar datos del usero al editar
    LaunchedEffect(selectedUser) {
        if (userId != null && selectedUser != null) {
            email = selectedUser?.email ?: ""
            displayName = selectedUser?.displayName ?: ""
            phoneNumber = selectedUser?.phoneNumber ?: ""
            userType = selectedUser?.userType
        }
    }

    // Si es edición, cargar el usero
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.users.value.find { it.uid == userId }?.let {
                viewModel.setSelectedUser(it)
            }
        } else {
            // Si es creación, resetear el formulario
            email = ""
            displayName = ""
            phoneNumber = ""
            userType = UserType.CLIENT
            active = true
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (userId == null) "Crear Usuario" else "Editar Usuario",
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
                if (userId == null) "Crear Nuevo Usuario" else "Editar Usuario",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize)
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email del usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = email.isBlank()
            )

            // Campo DisplayName
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nombre del usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = displayName.isBlank()
            )

            // Campo Teléfono
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de contacto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                placeholder = { Text("+56 9 1234 5678") }
            )

            // Selector de Tipo de Usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tipo de Usuario",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = userType == UserType.CLIENT,
                            onClick = { userType = UserType.CLIENT },
                            label = { Text("Cliente", fontSize = fontSize * 0.9f) }
                        )
                        FilterChip(
                            selected = userType == UserType.ADMIN,
                            onClick = { userType = UserType.ADMIN },
                            label = { Text("Administrador", fontSize = fontSize * 0.9f) }
                        )
                    }
                }
            }

            // Switch para Estado Activo (solo visible en edición)
            if (userId != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                "Usuario Activo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                            )
                            Text(
                                if (active) "El usuario puede acceder al sistema"
                                else "El usuario está desactivado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = fontSize * 0.8f,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Switch(
                            checked = active,
                            onCheckedChange = { active = it }
                        )
                    }
                }
            } else {
                // En creación, mostrar indicador de que será activo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Activo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Nuevo usuario creado como activo",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                        )
                    }
                }
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
                    if (validateForm(email, displayName, phoneNumber, userType)) {
                        val user = UserProfile(
                            email = email,
                            displayName = displayName,
                            phoneNumber = phoneNumber,
                            userType = userType,
                            active = active
                        )

                        if (userId == null) {
                            viewModel.createUser(user)
                        } else {
                            viewModel.updateUser(userId, user)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && validateForm(email, displayName, phoneNumber, userType)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (userId == null) "Crear Usuario" else "Actualizar Usuario")
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
fun validateForm(
    email: String,
    displayName: String,
    phoneNumber: String,
    userType: UserType?,
): Boolean {
    return email.isNotBlank() &&
            displayName.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            userType.toString().isNotBlank()
}