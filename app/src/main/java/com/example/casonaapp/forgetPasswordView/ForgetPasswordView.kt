package com.example.casonaapp.forgetPasswordView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.casonaapp.R
import com.example.casonaapp.ui.theme.CasonaFontFamily
import com.example.casonaapp.viewmodels.FontSizeViewModel
import com.example.casonaapp.viewmodels.LocalFontSize

@Composable
fun ForgetPasswordView(
    viewModel: FontSizeViewModel,
    onBack: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val fontSize = LocalFontSize.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "Casona Encantada Icon",
            modifier = Modifier.size(100.dp)
        )

        Text(
            "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = fontSize
            ),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            fontFamily = CasonaFontFamily
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = "" // limpiar error al escribir
            },
            label = { Text("Correo electrónico",
                fontSize = fontSize,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize)) },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = fontSize

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "Debe introducir un correo electrónico"
                } else {
                    showDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar instrucciones", fontSize = fontSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Volver al Login", fontSize = fontSize)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 100.dp)
        ) {
            Button(onClick = { viewModel.decreaseFontSize() }) {
                Text("-", fontSize = fontSize)
            }
            Button(onClick = { viewModel.increaseFontSize() }) {
                Text("+", fontSize = fontSize)
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Instrucciones enviadas") },
            text = { Text("Se han enviado las instrucciones al correo: $email") }
        )
    }
}
