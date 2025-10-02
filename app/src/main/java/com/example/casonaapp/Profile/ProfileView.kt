package com.example.casonaapp.Profile

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.casonaapp.components.SimpleTopAppBar
import com.example.casonaapp.viewmodels.LocalFontSize
import com.example.casonaapp.viewmodels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun ProfileView(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val fontSize = LocalFontSize.current
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    // Estados para edición
    var editingDisplayName by remember { mutableStateOf(false) }
    var editingEmail by remember { mutableStateOf(false) }
    var editingPhone by remember { mutableStateOf(false) }

    var tempDisplayName by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // Resetear estados cuando cambia el perfil
    LaunchedEffect(userProfile) {
        userProfile?.let {
            tempDisplayName = it.displayName
            tempEmail = it.email
            tempPhone = it.phoneNumber ?: ""
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Mi Perfil",
                onMenuClick = onBack,
                customIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        if (isLoading && userProfile == null) {
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (editingDisplayName) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = tempDisplayName,
                                    onValueChange = { tempDisplayName = it },
                                    label = { Text("Nombre") },
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        if (tempDisplayName.isNotBlank()) {
                                            viewModel.updateDisplayName(tempDisplayName)
                                            editingDisplayName = false
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Check, "Guardar")
                                }
                                IconButton(
                                    onClick = {
                                        editingDisplayName = false
                                        tempDisplayName = userProfile?.displayName ?: ""
                                    }
                                ) {
                                    Icon(Icons.Default.Close, "Cancelar")
                                }
                            }
                        } else {
                            Text(
                                text = userProfile?.displayName ?: "Sin nombre",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            TextButton(
                                onClick = {
                                    tempDisplayName = userProfile?.displayName ?: ""
                                    editingDisplayName = true
                                }
                            ) {
                                Text("Editar nombre")
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Información de la cuenta",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        // Email
                        if (editingEmail) {
                            Column {
                                OutlinedTextField(
                                    value = tempEmail,
                                    onValueChange = { tempEmail = it },
                                    label = { Text("Correo electrónico") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    )
                                )
                                Row {
                                    TextButton(
                                        onClick = {
                                            if (tempEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches()) {
                                                viewModel.updateEmail(tempEmail)
                                                editingEmail = false
                                            }
                                        }
                                    ) {
                                        Text("Guardar")
                                    }
                                    TextButton(
                                        onClick = {
                                            editingEmail = false
                                            tempEmail = userProfile?.email ?: ""
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        } else {
                            ProfileField(
                                label = "Correo electrónico",
                                value = userProfile?.email ?: "",
                                onEdit = {
                                    tempEmail = userProfile?.email ?: ""
                                    editingEmail = true                                }
                            )
                        }

                        // Número de teléfono
                        if (editingPhone) {
                            Column {
                                OutlinedTextField(
                                    value = tempPhone,
                                    onValueChange = { tempPhone = it },
                                    label = { Text("Número de contacto") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone
                                    ),
                                    placeholder = { Text("+56 9 1234 5678") }
                                )
                                Row {
                                    TextButton(
                                        onClick = {
                                            if (tempPhone.isNotBlank()) {
                                                viewModel.updatePhoneNumber(tempPhone)
                                                editingPhone = false
                                            }
                                        }
                                    ) {
                                        Text("Guardar")
                                    }
                                    TextButton(
                                        onClick = {
                                            editingPhone = false
                                            tempPhone = userProfile?.phoneNumber ?: ""
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        } else {
                            ProfileField(
                                label = "Número de contacto",
                                value = userProfile?.phoneNumber ?: "No agregado",
                                onEdit = {
                                    tempPhone = userProfile?.phoneNumber ?: ""
                                    editingPhone = true
                                }
                            )
                        }

                        // Fecha de creación
                        ProfileField(
                            label = "Miembro desde",
                            value = userProfile?.createdAt?.let { date ->
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                            } ?: "Fecha no disponible"
                        )

                        // Último inicio de sesión
                        ProfileField(
                            label = "Último acceso",
                            value = userProfile?.lastLogin?.let { date ->
                                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
                            } ?: "Fecha no disponible"
                        )
                    }
                }

                // Mostrar mensajes
                message?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.contains("éxito", ignoreCase = true) || msg.contains("enviado", ignoreCase = true)) {
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
                                text = msg,
                                modifier = Modifier.weight(1f),
                                color = if (msg.contains("éxito", ignoreCase = true) || msg.contains("enviado", ignoreCase = true)) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                }
                            )
                            IconButton(
                                onClick = { viewModel.clearMessage() }
                            ) {
                                Icon(Icons.Default.Close, "Cerrar")
                            }
                        }
                    }
                }

                // Estadísticas
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Estadísticas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text("Próximamente...", fontSize = fontSize)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onEdit: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        onEdit?.let {
            IconButton(onClick = it) {
                Icon(Icons.Default.Edit, "Editar")
            }
        }
    }
}