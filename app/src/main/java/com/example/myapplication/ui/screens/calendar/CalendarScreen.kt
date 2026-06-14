package com.example.myapplication.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.entity.EventEntity
import com.example.myapplication.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    onNavigateToPlans: () -> Unit,
    onNavigateToJoin: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by calendarViewModel.uiState.collectAsState()
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedDayEvents by remember { mutableStateOf<List<EventEntity>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                    }
                    IconButton(onClick = onNavigateToPlans) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Plans")
                    }
                    IconButton(onClick = onNavigateToJoin) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Join")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEventDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CalendarHeader(
                currentMonth = uiState.currentMonth,
                onPreviousMonth = { calendarViewModel.changeMonth(-1) },
                onNextMonth = { calendarViewModel.changeMonth(1) }
            )
            CalendarGrid(
                currentMonth = uiState.currentMonth,
                events = uiState.events,
                onDaySelected = { date ->
                    calendarViewModel.selectDate(date)
                    selectedDayEvents = uiState.events.filter { event ->
                        val cal = Calendar.getInstance().apply { timeInMillis = date }
                        cal.set(Calendar.HOUR_OF_DAY, 0)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        val startOfDay = cal.timeInMillis
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                        val endOfDay = cal.timeInMillis
                        event.startTime in startOfDay until endOfDay
                    }
                }
            )
            if (selectedDayEvents.isNotEmpty()) {
                Text(
                    text = "Events",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedDayEvents) { event ->
                        EventCard(event = event)
                    }
                }
            }
        }
    }

    if (showEventDialog) {
        EventFormDialog(
            onDismiss = { showEventDialog = false },
            onSave = { title, description, startTime, endTime, location, color ->
                calendarViewModel.createEvent(
                    title, description, startTime, endTime, location, color
                )
                showEventDialog = false
            }
        )
    }
}

@Composable
fun CalendarHeader(
    currentMonth: Long,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }
        Text(
            text = dateFormat.format(Date(currentMonth)),
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        days.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Long,
    events: List<EventEntity>,
    onDaySelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentMonth }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(year, month, 1)
    }
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startingDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    val todayCal = Calendar.getInstance()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        var dayCounter = 1
        val totalCells = startingDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < startingDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.size(40.dp))
                    } else {
                        val day = dayCounter
                        val dateCal = Calendar.getInstance().apply {
                            set(year, month, day)
                        }
                        val dateMillis = dateCal.timeInMillis
                        val hasEvent = events.any { event ->
                            val eCal = Calendar.getInstance().apply { timeInMillis = event.startTime }
                            eCal.get(Calendar.YEAR) == year &&
                                    eCal.get(Calendar.MONTH) == month &&
                                    eCal.get(Calendar.DAY_OF_MONTH) == day
                        }
                        val isToday = todayCal.get(Calendar.YEAR) == year &&
                                todayCal.get(Calendar.MONTH) == month &&
                                todayCal.get(Calendar.DAY_OF_MONTH) == day

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { onDaySelected(dateMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isToday) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                if (hasEvent) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventEntity) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
