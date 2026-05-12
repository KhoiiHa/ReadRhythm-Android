package com.khoiha.readrhythm.ui.bookdetail

import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingSessionEntity

data class BookDetailUiState(
    val isLoading: Boolean = true,
    val isSavingSession: Boolean = false,
    val book: BookEntity? = null,
    val sessions: List<ReadingSessionEntity> = emptyList(),
    val errorMessage: String? = null
)
