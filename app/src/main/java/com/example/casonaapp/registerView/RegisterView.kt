package com.example.casonaapp.registerView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import com.example.casonaapp.data.UserPreferences
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.casonaapp.R
import com.example.casonaapp.ui.theme.CasonaFontFamily
import com.example.casonaapp.viewmodels.FontSizeViewModel
import com.example.casonaapp.viewmodels.LocalFontSize

@Composable
fun RegisterView(
    viewModel: FontSizeViewModel,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val fontSize = LocalFontSize.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "Casona Encantada Icon",
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Registrarse",
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
            onValueChange = { email = it },
            label = {
                Text("Correo electrónico", fontSize = fontSize,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize)
                )
            },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Contraseña",
                    fontSize = fontSize,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize)
                )
            },
            textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        )

        if (message.isNotEmpty()) {
            Text(text = message, fontSize = fontSize, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        userPrefs.saveUser(email, password)
                        onRegisterSuccess() // navega a HomeView
                    }
                } else {
                    message = "Por favor completa todos los campos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse", fontSize = fontSize,)
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
}
