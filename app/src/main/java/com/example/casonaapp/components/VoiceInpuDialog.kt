package com.example.casonaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.casonaapp.utils.SpeechRecognitionHelper
import com.example.casonaapp.utils.SpeechRecognitionResult
import com.example.casonaapp.utils.rememberAudioPermissionState
import kotlinx.coroutines.launch

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onTextRecognized: (String) -> Unit,
    fieldName: String = "campo"
) {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentState by remember { mutableStateOf("Listo para comenzar") }

    val audioPermissionState = rememberAudioPermissionState()
    val speechHelper = remember { SpeechRecognitionHelper(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!audioPermissionState.hasPermission) {
            audioPermissionState.requestPermission()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Reconocimiento de Voz - $fieldName",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Estado actual
                Text(
                    currentState,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Icono animado según el estado
                when {
                    isRecording -> {
                        // Animación de grabación
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 4.dp
                        )
                    }
                    recognizedText.isNotEmpty() -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Reconocido",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Micrófono",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar texto reconocido
                if (recognizedText.isNotEmpty()) {
                    OutlinedTextField(
                        value = recognizedText,
                        onValueChange = { recognizedText = it },
                        label = { Text("Texto reconocido") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                // Mostrar error
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            speechHelper.destroy()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    if (isRecording) {
                        Button(
                            onClick = {
                                isRecording = false
                                speechHelper.stopListening()
                                currentState = "Grabación detenida"
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Detener")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (!audioPermissionState.hasPermission) {
                                    errorMessage = "Se necesita permiso de micrófono"
                                    audioPermissionState.requestPermission()
                                    return@Button
                                }

                                isRecording = true
                                errorMessage = null
                                recognizedText = ""
                                currentState = "Escuchando... Habla ahora"

                                coroutineScope.launch {
                                    speechHelper.startListening().collect { result ->
                                        when (result) {
                                            is SpeechRecognitionResult.Listening -> {
                                                currentState = "Escuchando..."
                                            }
                                            is SpeechRecognitionResult.Speaking -> {
                                                currentState = "Habla detectada..."
                                            }
                                            is SpeechRecognitionResult.Partial -> {
                                                recognizedText = result.text
                                                currentState = "Reconociendo..."
                                            }
                                            is SpeechRecognitionResult.Success -> {
                                                recognizedText = result.text
                                                currentState = "Texto reconocido!"
                                                isRecording = false
                                            }
                                            is SpeechRecognitionResult.Error -> {
                                                errorMessage = result.message
                                                currentState = "Error: ${result.message}"
                                                isRecording = false
                                            }
                                            else -> {
                                                // Otros estados
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Comenzar")
                        }
                    }

                    if (recognizedText.isNotEmpty()) {
                        Button(
                            onClick = {
                                speechHelper.destroy()
                                onTextRecognized(recognizedText)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Usar Texto")
                        }
                    }
                }
            }
        }
    }

    // Limpiar cuando el diálogo se cierre
    DisposableEffect(Unit) {
        onDispose {
            speechHelper.destroy()
        }
    }
}