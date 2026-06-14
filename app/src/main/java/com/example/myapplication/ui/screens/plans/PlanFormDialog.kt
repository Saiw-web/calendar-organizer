package com.example.myapplication.ui.screens.plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
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
fun PlanFormDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, description: String?, dueDate: Long?, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDueDate by remember { mutableStateOf<Long?>(null) }
    var priority by remember { mutableStateOf("medium") }
    var priorityExpanded by remember { mutableStateOf(false) }
    val priorities = listOf("low", "medium", "high")

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDueDate
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Plan") },
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
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = priority.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    priority = p
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        if (selectedDueDate != null) "Due date selected"
                        else "Set due date"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description.ifBlank { null }, selectedDueDate, priority)
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
                    selectedDueDate = datePickerState.selectedDateMillis
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
