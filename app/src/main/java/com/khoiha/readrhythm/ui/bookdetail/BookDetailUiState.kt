package com.khoiha.readrhythm.ui.bookdetail

import com.khoiha.readrhythm.data.local.BookEntity

data class BookDetailUiState(
    val isLoading: Boolean = true,
    val book: BookEntity? = null,
    val errorMessage: String? = null
)
