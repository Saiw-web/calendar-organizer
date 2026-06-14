package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.PlanDao
import com.example.myapplication.data.local.entity.PlanEntity
import com.example.myapplication.data.remote.api.ApiService
import com.example.myapplication.data.remote.model.CreatePlanRequest
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PlanRepository(
    private val apiService: ApiService,
    private val planDao: PlanDao
) {
    fun getAllPlans(): Flow<List<PlanEntity>> = planDao.getAllPlans()

    fun getPlansByStatus(status: String): Flow<List<PlanEntity>> = planDao.getPlansByStatus(status)

    suspend fun createPlan(
        title: String,
        description: String?,
        dueDate: Long?,
        priority: String,
        status: String
    ): Result<PlanEntity> {
        val localPlan = PlanEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority,
            status = status,
            isSynced = false
        )
        planDao.insertPlan(localPlan)
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val request = CreatePlanRequest(
                title = title,
                description = description,
                dueDate = dueDate?.let { dateFormat.format(Date(it)) },
                priority = priority,
                status = status
            )
            val response = apiService.createPlan(request)
            if (response.isSuccessful) {
                val synced = localPlan.copy(isSynced = true)
                planDao.updatePlan(synced)
                Result.success(synced)
            } else {
                Result.success(localPlan)
            }
        } catch (e: Exception) {
            Result.success(localPlan)
        }
    }

    suspend fun updatePlan(plan: PlanEntity): Result<PlanEntity> {
        val updated = plan.copy(updatedAt = System.currentTimeMillis())
        planDao.updatePlan(updated)
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val request = CreatePlanRequest(
                title = plan.title,
                description = plan.description,
                dueDate = plan.dueDate?.let { dateFormat.format(Date(it)) },
                priority = plan.priority,
                status = plan.status
            )
            val response = apiService.updatePlan(plan.id, request)
            if (response.isSuccessful) {
                val synced = updated.copy(isSynced = true)
                planDao.updatePlan(synced)
                Result.success(synced)
            } else {
                Result.success(updated)
            }
        } catch (e: Exception) {
            Result.success(updated)
        }
    }

    suspend fun deletePlan(plan: PlanEntity) {
        planDao.deletePlan(plan)
        try {
            apiService.deletePlan(plan.id)
        } catch (_: Exception) {}
    }

    suspend fun syncPlans() {
        try {
            val response = apiService.getPlans()
            if (response.isSuccessful) {
                val remotePlans = response.body() ?: return
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'X'", Locale.US)
                val entities = remotePlans.map { dto ->
                    PlanEntity(
                        id = dto.id,
                        title = dto.title,
                        description = dto.description,
                        dueDate = dto.dueDate?.let { dateFormat.parse(it)?.time },
                        priority = dto.priority,
                        status = dto.status,
                        isSynced = true
                    )
                }
                entities.forEach { planDao.insertPlan(it) }
            }
        } catch (_: Exception) {}
    }
}
