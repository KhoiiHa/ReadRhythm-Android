package com.khoiha.readrhythm.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.ReadingRepository
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val readingRepository: ReadingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        observeBooks()
    }

    private fun observeBooks() {
        viewModelScope.launch {
            readingRepository.observeBooks()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Could not load your library."
                        )
                    }
                }
                .collect { books ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            books = books,
                            filteredBooks = filterBooks(
                                books = books,
                                query = it.searchQuery,
                                filter = it.selectedFilter
                            ),
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredBooks = filterBooks(
                    books = it.books,
                    query = query,
                    filter = it.selectedFilter
                )
            )
        }
    }

    fun updateFormatFilter(filter: LibraryFormatFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                filteredBooks = filterBooks(
                    books = it.books,
                    query = it.searchQuery,
                    filter = filter
                )
            )
        }
    }

    fun addBook(
        title: String,
        author: String?,
        format: ReadingFormat,
        totalUnits: Int
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Title is required.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            try {
                readingRepository.insertBook(
                    BookEntity(
                        title = trimmedTitle,
                        author = author?.trim()?.takeIf { it.isNotEmpty() },
                        format = format,
                        progress = 0,
                        totalUnits = totalUnits.coerceAtLeast(0),
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Could not save this book.")
                }
            } finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            try {
                readingRepository.deleteBook(book)
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Could not delete this book.")
                }
            }
        }
    }

    private fun filterBooks(
        books: List<BookEntity>,
        query: String,
        filter: LibraryFormatFilter
    ): List<BookEntity> {
        val normalizedQuery = query.trim().lowercase()

        return books.filter { book ->
            val matchesQuery = normalizedQuery.isEmpty() ||
                book.title.lowercase().contains(normalizedQuery) ||
                book.author.orEmpty().lowercase().contains(normalizedQuery)
            val matchesFormat = filter.matches(book.format)

            matchesQuery && matchesFormat
        }
    }

    class Factory(
        private val readingRepository: ReadingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                return LibraryViewModel(readingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
