package com.example.myapplication.ui.screens.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.entity.PlanEntity
import com.example.myapplication.viewmodel.PlansViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    plansViewModel: PlansViewModel,
    onBack: () -> Unit
) {
    val uiState by plansViewModel.uiState.collectAsState()
    var showPlanDialog by remember { mutableStateOf(false) }
    val filteredPlans = plansViewModel.getFilteredPlans()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Future Plans") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showPlanDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("all", "active", "completed", "archived").forEach { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { plansViewModel.setFilter(filter) },
                        label = {
                            Text(
                                filter.replaceFirstChar { it.uppercase() }
                            )
                        }
                    )
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPlans, key = { it.id }) { plan ->
                    PlanCard(
                        plan = plan,
                        onToggleStatus = {
                            val newStatus = if (plan.status == "completed") "active" else "completed"
                            plansViewModel.updatePlan(plan.copy(status = newStatus))
                        },
                        onDelete = { plansViewModel.deletePlan(plan) }
                    )
                }
            }
        }
    }

    if (showPlanDialog) {
        PlanFormDialog(
            onDismiss = { showPlanDialog = false },
            onSave = { title, description, dueDate, priority ->
                plansViewModel.createPlan(title, description, dueDate, priority)
                showPlanDialog = false
            }
        )
    }
}

@Composable
fun PlanCard(
    plan: PlanEntity,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.status == "completed")
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (plan.status == "completed")
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
                plan.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                plan.dueDate?.let {
                    Text(
                        text = "Due: ${dateFormat.format(Date(it))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onToggleStatus) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Toggle status",
                    tint = if (plan.status == "completed")
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
