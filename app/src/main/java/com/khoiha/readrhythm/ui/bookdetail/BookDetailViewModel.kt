package com.khoiha.readrhythm.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.ReadingRepository
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
                    _uiState.value = BookDetailUiState(
                        isLoading = false,
                        book = book,
                        errorMessage = if (book == null) "Book not found." else null
                    )
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
