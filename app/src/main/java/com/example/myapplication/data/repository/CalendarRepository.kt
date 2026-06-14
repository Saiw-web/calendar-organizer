package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.CalendarDao
import com.example.myapplication.data.local.entity.CalendarEntity
import com.example.myapplication.data.remote.api.ApiService
import com.example.myapplication.data.remote.model.CreateCalendarRequest
import com.example.myapplication.data.remote.model.JoinCalendarRequest
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CalendarRepository(
    private val apiService: ApiService,
    private val calendarDao: CalendarDao
) {
    fun getAllCalendars(): Flow<List<CalendarEntity>> = calendarDao.getAllCalendars()

    suspend fun createCalendar(name: String, description: String?): Result<CalendarEntity> {
        return try {
            val response = apiService.createCalendar(CreateCalendarRequest(name, description))
            if (response.isSuccessful) {
                val dto = response.body()!!
                val entity = CalendarEntity(
                    id = dto.id,
                    name = dto.name,
                    ownerEmail = dto.ownerEmail,
                    description = dto.description
                )
                calendarDao.insertCalendar(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("Failed to create calendar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinCalendar(code: String): Result<String> {
        return try {
            val response = apiService.joinCalendar(JoinCalendarRequest(code))
            if (response.isSuccessful) {
                val body = response.body()!!
                Result.success(body.calendarId)
            } else {
                Result.failure(Exception("Failed to join calendar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncCalendars() {
        try {
            val response = apiService.getCalendars()
            if (response.isSuccessful) {
                val calendars = response.body() ?: return
                calendars.forEach { dto ->
                    calendarDao.insertCalendar(
                        CalendarEntity(
                            id = dto.id,
                            name = dto.name,
                            ownerEmail = dto.ownerEmail,
                            description = dto.description
                        )
                    )
                }
            }
        } catch (_: Exception) {}
    }
}
