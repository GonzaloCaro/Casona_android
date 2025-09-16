package com.example.casonaapp.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.casonaapp.R
import com.example.casonaapp.data.UserPreferences
import com.example.casonaapp.ui.theme.CasonaFontFamily
import com.example.casonaapp.ui.theme.LightBlue
import com.example.casonaapp.ui.theme.Olive
import com.example.casonaapp.viewmodels.FontSizeViewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import com.example.casonaapp.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode

@Composable
fun LoginScreen(
    viewModel: FontSizeViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val scope = rememberCoroutineScope()

    val fontSize = LocalFontSize.current
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val currentColors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "Casona Encantada Icon",
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Casona EncantadApp",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = fontSize
            ),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            fontFamily = CasonaFontFamily
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    "Usuario",
                    fontSize = fontSize,
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
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = fontSize,
            )
        }

        Text(
            text = "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary,fontSize = fontSize),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clickable { onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    val storedUser = userPrefs.userFlow.collect { user ->
                        if (user != null && user.first == username && user.second == password) {
                            onLoginSuccess()
                        } else {
                            message = "Usuario o contraseña incorrectos"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión", fontSize = fontSize)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRegisterClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse", fontSize = fontSize)
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
            Button(
                onClick = { themeViewModel.toggleTheme() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme == true) LightBlue else Olive
                )
            ) {
                Icon(
                    imageVector = if (isDarkTheme == true) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Modo noche"
                )
            }
        }

    }
}
