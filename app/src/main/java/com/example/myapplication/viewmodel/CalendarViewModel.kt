package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.EventEntity
import com.example.myapplication.data.repository.EventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarUiState(
    val currentMonth: Long = Calendar.getInstance().timeInMillis,
    val selectedDate: Long? = null,
    val events: List<EventEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val calendarId: String? = null
)

class CalendarViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun setCalendar(calendarId: String) {
        _uiState.value = _uiState.value.copy(calendarId = calendarId)
        viewModelScope.launch {
            eventRepository.getEventsForCalendar(calendarId).collect { events ->
                _uiState.value = _uiState.value.copy(events = events)
            }
        }
    }

    private val _selectedDate = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventsForSelectedDay = _selectedDate.flatMapLatest { date ->
        val calendarId = _uiState.value.calendarId ?: return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
        if (date != null) {
            eventRepository.getEventsForDay(calendarId, date)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }

    fun selectDate(date: Long?) {
        _selectedDate.value = date
    }

    fun changeMonth(offset: Int) {
        val cal = Calendar.getInstance().apply { timeInMillis = _uiState.value.currentMonth }
        cal.add(Calendar.MONTH, offset)
        _uiState.value = _uiState.value.copy(currentMonth = cal.timeInMillis)
    }

    fun createEvent(
        title: String,
        description: String?,
        startTime: Long,
        endTime: Long,
        location: String?,
        color: Int?
    ) {
        val calendarId = _uiState.value.calendarId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            eventRepository.createEvent(calendarId, title, description, startTime, endTime, location, color)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun deleteEvent(event: EventEntity) {
        val calendarId = _uiState.value.calendarId ?: return
        viewModelScope.launch {
            eventRepository.deleteEvent(calendarId, event)
        }
    }

    fun syncEvents() {
        val calendarId = _uiState.value.calendarId ?: return
        viewModelScope.launch {
            eventRepository.syncEvents(calendarId)
        }
    }

    class Factory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(eventRepository) as T
        }
    }
}
