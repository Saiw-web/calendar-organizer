package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.remote.api.RetrofitClient
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.CalendarRepository
import com.example.myapplication.data.repository.EventRepository
import com.example.myapplication.data.repository.PlanRepository

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var eventRepository: EventRepository
        private set

    lateinit var planRepository: PlanRepository
        private set

    lateinit var calendarRepository: CalendarRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)

        val apiService = RetrofitClient.apiService
        val profileDao = database.profileDao()
        val eventDao = database.eventDao()
        val planDao = database.planDao()
        val calendarDao = database.calendarDao()

        authRepository = AuthRepository(apiService, profileDao)
        eventRepository = EventRepository(apiService, eventDao)
        planRepository = PlanRepository(apiService, planDao)
        calendarRepository = CalendarRepository(apiService, calendarDao)
    }
}
