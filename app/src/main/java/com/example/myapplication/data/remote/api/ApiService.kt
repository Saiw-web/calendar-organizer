package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.model.AuthResponse
import com.example.myapplication.data.remote.model.CalendarDto
import com.example.myapplication.data.remote.model.CreateCalendarRequest
import com.example.myapplication.data.remote.model.CreateEventRequest
import com.example.myapplication.data.remote.model.CreatePlanRequest
import com.example.myapplication.data.remote.model.EventDto
import com.example.myapplication.data.remote.model.JoinCalendarRequest
import com.example.myapplication.data.remote.model.JoinCalendarResponse
import com.example.myapplication.data.remote.model.LoginRequest
import com.example.myapplication.data.remote.model.PlanDto
import com.example.myapplication.data.remote.model.RegisterRequest
import com.example.myapplication.data.remote.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getProfile(): Response<UserDto>

    @GET("api/calendars")
    suspend fun getCalendars(): Response<List<CalendarDto>>

    @POST("api/calendars")
    suspend fun createCalendar(@Body request: CreateCalendarRequest): Response<CalendarDto>

    @POST("api/calendars/join")
    suspend fun joinCalendar(@Body request: JoinCalendarRequest): Response<JoinCalendarResponse>

    @GET("api/calendars/{calendarId}/events")
    suspend fun getEvents(@Path("calendarId") calendarId: String): Response<List<EventDto>>

    @POST("api/calendars/{calendarId}/events")
    suspend fun createEvent(
        @Path("calendarId") calendarId: String,
        @Body request: CreateEventRequest
    ): Response<EventDto>

    @PUT("api/calendars/{calendarId}/events/{eventId}")
    suspend fun updateEvent(
        @Path("calendarId") calendarId: String,
        @Path("eventId") eventId: String,
        @Body request: CreateEventRequest
    ): Response<EventDto>

    @DELETE("api/calendars/{calendarId}/events/{eventId}")
    suspend fun deleteEvent(
        @Path("calendarId") calendarId: String,
        @Path("eventId") eventId: String
    ): Response<Unit>

    @GET("api/plans")
    suspend fun getPlans(): Response<List<PlanDto>>

    @POST("api/plans")
    suspend fun createPlan(@Body request: CreatePlanRequest): Response<PlanDto>

    @PUT("api/plans/{planId}")
    suspend fun updatePlan(
        @Path("planId") planId: String,
        @Body request: CreatePlanRequest
    ): Response<PlanDto>

    @DELETE("api/plans/{planId}")
    suspend fun deletePlan(@Path("planId") planId: String): Response<Unit>

    @GET("api/calendars/{calendarId}/events")
    suspend fun getEventsByDate(
        @Path("calendarId") calendarId: String,
        @Query("date") date: String
    ): Response<List<EventDto>>
}
