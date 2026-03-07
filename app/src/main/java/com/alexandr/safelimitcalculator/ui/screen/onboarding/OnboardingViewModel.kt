package com.alexandr.safelimitcalculator.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val isCompleting: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() {
        val currentState = _uiState.value
        if (currentState.currentStep < 1) { // 2 steps total (0 and 1)
            _uiState.value = currentState.copy(currentStep = currentState.currentStep + 1)
        }
    }

    fun previousStep() {
        val currentState = _uiState.value
        if (currentState.currentStep > 0) {
            _uiState.value = currentState.copy(currentStep = currentState.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCompleting = true)
                repository.setOnboardingShown(true)
                _uiState.value = _uiState.value.copy(isCompleting = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCompleting = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

