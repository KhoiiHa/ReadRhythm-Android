package com.khoiha.readrhythm.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khoiha.readrhythm.data.AddDiscoverBookResult
import com.khoiha.readrhythm.data.DiscoverBook
import com.khoiha.readrhythm.data.DiscoverRepository
import com.khoiha.readrhythm.data.ReadingRepository
import com.khoiha.readrhythm.data.local.BookEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DiscoverViewModel(
    private val discoverRepository: DiscoverRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    private var libraryBooks: List<BookEntity> = emptyList()

    init {
        observeLibraryBooks()
    }

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
                    results = results.withLibraryState()
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
                        results = it.results.markBookAsInLibrary(book),
                        feedbackMessage = when (result) {
                            AddDiscoverBookResult.Saved ->
                                "Saved to Library. You can now track sessions from your Library."
                            AddDiscoverBookResult.AlreadyExists ->
                                "This title is already in your Library."
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

    private fun observeLibraryBooks() {
        viewModelScope.launch {
            readingRepository.observeBooks()
                .catch { libraryBooks = emptyList() }
                .collect { books ->
                    libraryBooks = books
                    _uiState.update {
                        it.copy(results = it.results.withLibraryState())
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

    private fun List<DiscoverBook>.withLibraryState(): List<DiscoverBook> {
        return map { book ->
            book.copy(
                isInLibrary = libraryBooks.any { libraryBook ->
                    libraryBook.matchesDiscoverBook(book)
                }
            )
        }
    }

    private fun List<DiscoverBook>.markBookAsInLibrary(savedBook: DiscoverBook): List<DiscoverBook> {
        return map { book ->
            if (book.matchesDiscoverBook(savedBook)) {
                book.copy(isInLibrary = true)
            } else {
                book
            }
        }
    }

    private fun BookEntity.matchesDiscoverBook(book: DiscoverBook): Boolean {
        val sourceMatches = sourceId != null && sourceId == book.sourceId
        val titleMatches = title.trim().equals(book.title.trim(), ignoreCase = true)
        val authorMatches = author.cleanKey().equals(book.firstAuthor.cleanKey(), ignoreCase = true)

        return sourceMatches || (titleMatches && authorMatches)
    }

    private fun DiscoverBook.matchesDiscoverBook(other: DiscoverBook): Boolean {
        val sourceMatches = sourceId == other.sourceId
        val titleMatches = title.trim().equals(other.title.trim(), ignoreCase = true)
        val authorMatches = firstAuthor.cleanKey().equals(other.firstAuthor.cleanKey(), ignoreCase = true)

        return sourceMatches || (titleMatches && authorMatches)
    }

    private fun String?.cleanKey(): String {
        return this?.trim().orEmpty()
    }
}
