package com.khoiha.readrhythm.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.ReadingRepository
import com.khoiha.readrhythm.data.local.ReadingSessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val readingRepository: ReadingRepository,
    private val bookId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState

    init {
        observeBook()
        observeSessions()
    }

    private fun observeBook() {
        viewModelScope.launch {
            readingRepository.observeBook(bookId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Could not load this book."
                        )
                    }
                }
                .collect { book ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            book = book,
                            errorMessage = if (book == null) "Book not found." else null
                        )
                    }
                }
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            readingRepository.observeSessionsForBook(bookId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Could not load sessions.")
                    }
                }
                .collect { sessions ->
                    _uiState.update {
                        it.copy(sessions = sessions)
                    }
                }
        }
    }

    fun addSession(
        minutes: Int,
        progressAmount: Int
    ) {
        if (minutes <= 0) {
            _uiState.update {
                it.copy(errorMessage = "Minutes must be greater than 0.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSavingSession = true, errorMessage = null)
            }

            try {
                readingRepository.addSession(
                    ReadingSessionEntity(
                        bookId = bookId,
                        minutes = minutes,
                        progressAmount = progressAmount.coerceAtLeast(0),
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Could not save session.")
                }
            } finally {
                _uiState.update {
                    it.copy(isSavingSession = false)
                }
            }
        }
    }

    class Factory(
        private val readingRepository: ReadingRepository,
        private val bookId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
                return BookDetailViewModel(readingRepository, bookId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
