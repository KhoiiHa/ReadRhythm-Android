package com.khoiha.readrhythm.ui.library

import com.khoiha.readrhythm.data.local.BookEntity

data class LibraryUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val books: List<BookEntity> = emptyList(),
    val errorMessage: String? = null
)
