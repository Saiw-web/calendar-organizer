package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans ORDER BY dueDate ASC")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE status = :status ORDER BY dueDate ASC")
    fun getPlansByStatus(status: String): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE id = :id")
    suspend fun getPlanById(id: String): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun deletePlanById(id: String)

    @Query("SELECT * FROM plans WHERE isSynced = 0")
    suspend fun getUnsyncedPlans(): List<PlanEntity>
}
