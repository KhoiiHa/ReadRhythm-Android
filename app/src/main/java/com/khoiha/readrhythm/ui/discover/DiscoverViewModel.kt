package com.khoiha.readrhythm.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.AddDiscoverBookResult
import com.khoiha.readrhythm.data.DiscoverBook
import com.khoiha.readrhythm.data.DiscoverRepository
import com.khoiha.readrhythm.data.ReadingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DiscoverViewModel(
    private val discoverRepository: DiscoverRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    fun search(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            _uiState.update {
                it.copy(
                    hasSearched = false,
                    results = emptyList(),
                    errorMessage = null,
                    feedbackMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    hasSearched = true,
                    errorMessage = null,
                    feedbackMessage = null
                )
            }

            try {
                val results = discoverRepository.searchBooks(trimmedQuery)
                _uiState.value = DiscoverUiState(
                    isLoading = false,
                    hasSearched = true,
                    results = results
                )
            } catch (error: Exception) {
                _uiState.value = DiscoverUiState(
                    isLoading = false,
                    hasSearched = true,
                    errorMessage = searchErrorMessage(error)
                )
            }
        }
    }

    fun addToLibrary(book: DiscoverBook) {
        viewModelScope.launch {
            try {
                val result = readingRepository.addDiscoverBook(book)
                _uiState.update {
                    it.copy(
                        feedbackMessage = when (result) {
                            AddDiscoverBookResult.Saved -> "Saved to library"
                            AddDiscoverBookResult.AlreadyExists -> "Already in library"
                        },
                        errorMessage = null
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        feedbackMessage = null,
                        errorMessage = "Could not save this title. Please try again."
                    )
                }
            }
        }
    }

    private fun searchErrorMessage(error: Exception): String {
        return if (error is HttpException && error.code() == 429) {
            "Too many searches. Please wait a moment and try again."
        } else {
            "Search failed. Check your connection and try again."
        }
    }

    class Factory(
        private val discoverRepository: DiscoverRepository,
        private val readingRepository: ReadingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
                return DiscoverViewModel(discoverRepository, readingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
