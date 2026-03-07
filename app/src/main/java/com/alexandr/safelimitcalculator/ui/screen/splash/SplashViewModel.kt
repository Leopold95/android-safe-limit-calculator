package com.alexandr.safelimitcalculator.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class SplashUiState(
    val isInitialized: Boolean = false,
    val hasOnboarded: Boolean = false,
    val isLoading: Boolean = true
)

class SplashViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Initialize storage and load settings
                val hasOnboarded = repository.onboardingShownFlow.firstOrNull() ?: false
                _uiState.value = SplashUiState(
                    isInitialized = true,
                    hasOnboarded = hasOnboarded,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = SplashUiState(
                    isInitialized = false,
                    hasOnboarded = false,
                    isLoading = false
                )
            }
        }
    }
}

