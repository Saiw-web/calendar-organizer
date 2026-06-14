package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.PlanEntity
import com.example.myapplication.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlansUiState(
    val plans: List<PlanEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String = "all"
)

class PlansViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlansUiState())
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            planRepository.getAllPlans().collect { plans ->
                _uiState.value = _uiState.value.copy(plans = plans)
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun getFilteredPlans(): List<PlanEntity> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            "active" -> state.plans.filter { it.status == "active" }
            "completed" -> state.plans.filter { it.status == "completed" }
            "archived" -> state.plans.filter { it.status == "archived" }
            else -> state.plans
        }
    }

    fun createPlan(
        title: String,
        description: String?,
        dueDate: Long?,
        priority: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            planRepository.createPlan(title, description, dueDate, priority, "active")
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updatePlan(plan: PlanEntity) {
        viewModelScope.launch {
            planRepository.updatePlan(plan)
        }
    }

    fun deletePlan(plan: PlanEntity) {
        viewModelScope.launch {
            planRepository.deletePlan(plan)
        }
    }

    fun syncPlans() {
        viewModelScope.launch {
            planRepository.syncPlans()
        }
    }

    class Factory(private val planRepository: PlanRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlansViewModel(planRepository) as T
        }
    }
}
