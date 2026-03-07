package com.alexandr.safelimitcalculator.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val currentRoute: String = "home",
    val isNavigationReady: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Initialize main screen data
                _uiState.value = _uiState.value.copy(
                    isNavigationReady = true,
                    hasError = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isNavigationReady = true,
                    hasError = true,
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateCurrentRoute(route: String) {
        _uiState.value = _uiState.value.copy(currentRoute = route)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            hasError = false,
            errorMessage = null
        )
    }
}

