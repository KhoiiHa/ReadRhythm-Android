package com.khoiha.readrhythm.ui.library

import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat

data class LibraryUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val books: List<BookEntity> = emptyList(),
    val filteredBooks: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: LibraryFormatFilter = LibraryFormatFilter.ALL,
    val errorMessage: String? = null
)

enum class LibraryFormatFilter {
    ALL,
    BOOKS,
    AUDIOBOOKS
}

fun LibraryFormatFilter.matches(format: ReadingFormat): Boolean {
    return when (this) {
        LibraryFormatFilter.ALL -> true
        LibraryFormatFilter.BOOKS -> format == ReadingFormat.BOOK
        LibraryFormatFilter.AUDIOBOOKS -> format == ReadingFormat.AUDIOBOOK
    }
}
