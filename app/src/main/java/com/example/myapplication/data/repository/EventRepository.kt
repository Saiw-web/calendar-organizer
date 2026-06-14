package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.EventDao
import com.example.myapplication.data.local.entity.EventEntity
import com.example.myapplication.data.remote.api.ApiService
import com.example.myapplication.data.remote.model.CreateEventRequest
import com.example.myapplication.data.remote.model.EventDto
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    fun getEventsForCalendar(calendarId: String): Flow<List<EventEntity>> {
        return eventDao.getEventsByCalendar(calendarId)
    }

    fun getEventsForDay(calendarId: String, date: Long): Flow<List<EventEntity>> {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = date }
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val endOfDay = cal.timeInMillis
        return eventDao.getEventsForDay(calendarId, startOfDay, endOfDay)
    }

    suspend fun createEvent(
        calendarId: String,
        title: String,
        description: String?,
        startTime: Long,
        endTime: Long,
        location: String?,
        color: Int?
    ): Result<EventEntity> {
        val localEvent = EventEntity(
            id = UUID.randomUUID().toString(),
            calendarId = calendarId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            location = location,
            color = color,
            isSynced = false
        )
        eventDao.insertEvent(localEvent)
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val request = CreateEventRequest(
                calendarId = calendarId,
                title = title,
                description = description,
                startTime = dateFormat.format(Date(startTime)),
                endTime = dateFormat.format(Date(endTime)),
                location = location,
                color = color
            )
            val response = apiService.createEvent(calendarId, request)
            if (response.isSuccessful) {
                val synced = localEvent.copy(isSynced = true)
                eventDao.updateEvent(synced)
                Result.success(synced)
            } else {
                Result.success(localEvent)
            }
        } catch (e: Exception) {
            Result.success(localEvent)
        }
    }

    suspend fun updateEvent(
        calendarId: String,
        event: EventEntity
    ): Result<EventEntity> {
        val updated = event.copy(updatedAt = System.currentTimeMillis())
        eventDao.updateEvent(updated)
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val request = CreateEventRequest(
                calendarId = calendarId,
                title = event.title,
                description = event.description,
                startTime = dateFormat.format(Date(event.startTime)),
                endTime = dateFormat.format(Date(event.endTime)),
                location = event.location,
                color = event.color
            )
            val response = apiService.updateEvent(calendarId, event.id, request)
            if (response.isSuccessful) {
                val synced = updated.copy(isSynced = true)
                eventDao.updateEvent(synced)
                Result.success(synced)
            } else {
                Result.success(updated)
            }
        } catch (e: Exception) {
            Result.success(updated)
        }
    }

    suspend fun deleteEvent(calendarId: String, event: EventEntity) {
        eventDao.deleteEvent(event)
        try {
            apiService.deleteEvent(calendarId, event.id)
        } catch (_: Exception) {}
    }

    suspend fun syncEvents(calendarId: String) {
        try {
            val response = apiService.getEvents(calendarId)
            if (response.isSuccessful) {
                val remoteEvents = response.body() ?: return
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'X'", Locale.US)
                val entities = remoteEvents.map { dto ->
                    EventEntity(
                        id = dto.id,
                        calendarId = dto.calendarId,
                        title = dto.title,
                        description = dto.description,
                        startTime = dateFormat.parse(dto.startTime)?.time ?: System.currentTimeMillis(),
                        endTime = dateFormat.parse(dto.endTime)?.time ?: System.currentTimeMillis(),
                        location = dto.location,
                        color = dto.color,
                        isSynced = true
                    )
                }
                eventDao.insertEvents(entities)
            }
        } catch (_: Exception) {}
    }
}
