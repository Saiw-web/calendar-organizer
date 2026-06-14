package com.example.myapplication.ui.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, description: String?, startTime: Long, endTime: Long, location: String?, color: Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Event") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Select Date")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val cal = Calendar.getInstance()
                        val startCal = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                        }
                        val endCal = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1)
                            set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                        }
                        onSave(
                            title,
                            description.ifBlank { null },
                            startCal.timeInMillis,
                            endCal.timeInMillis,
                            location.ifBlank { null },
                            null
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
