package com.khoiha.readrhythm.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.ReadingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InsightsViewModel(
    private val readingRepository: ReadingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState

    init {
        observeInsights()
    }

    private fun observeInsights() {
        viewModelScope.launch {
            readingRepository.observeInsights()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Could not load insights."
                        )
                    }
                }
                .collect { summary ->
                    _uiState.value = InsightsUiState(
                        isLoading = false,
                        totalMinutes = summary.totalMinutes,
                        totalSessions = summary.totalSessions,
                        activeTitles = summary.activeTitles,
                        completedTitles = summary.completedTitles
                    )
                }
        }
    }

    class Factory(
        private val readingRepository: ReadingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                return InsightsViewModel(readingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
