package com.example.casonaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialDate: Long = System.currentTimeMillis()
) {
    var selectedDate by remember { mutableStateOf(initialDate) }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Seleccionar fecha y hora",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de fecha (simulado - en una app real usarías DatePicker)
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(selectedDate)),
                        onValueChange = { }, // Solo lectura
                        label = { Text("Fecha del evento") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, "Seleccionar fecha")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Nota: A Futuro se implementara un DatePicker como corresponde.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de incremento/decremento para simular cambio de fecha
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedDate -= 24 * 60 * 60 * 1000 // Restar 1 día
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("-1 día")
                        }

                        OutlinedButton(
                            onClick = {
                                selectedDate += 24 * 60 * 60 * 1000 // Sumar 1 día
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1 día")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                onDateSelected(selectedDate)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Seleccionar")
                        }
                    }
                }
            }
        }
    }
}