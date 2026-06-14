package com.example.myapplication.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)

data class EventDto(
    val id: String,
    val calendarId: String,
    val title: String,
    val description: String? = null,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val location: String? = null,
    val color: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CreateEventRequest(
    val calendarId: String,
    val title: String,
    val description: String? = null,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val location: String? = null,
    val color: Int? = null
)

data class PlanDto(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null,
    val priority: String = "medium",
    val status: String = "active",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CreatePlanRequest(
    val title: String,
    val description: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null,
    val priority: String = "medium",
    val status: String = "active"
)

data class CalendarDto(
    val id: String,
    val name: String,
    val ownerEmail: String,
    val description: String? = null,
    val color: Int? = null
)

data class CreateCalendarRequest(
    val name: String,
    val description: String? = null,
    val color: Int? = null
)

data class JoinCalendarRequest(
    val code: String
)

data class JoinCalendarResponse(
    val calendarId: String,
    val message: String
)

data class SyncPayload<T>(
    val data: T,
    val timestamp: Long = System.currentTimeMillis()
)

data class ErrorResponse(
    val error: String,
    val message: String? = null
)
