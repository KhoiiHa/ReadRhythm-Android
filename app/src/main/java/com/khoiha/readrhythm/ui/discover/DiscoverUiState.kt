package com.khoiha.readrhythm.ui.discover

import com.khoiha.readrhythm.data.DiscoverBook

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val results: List<DiscoverBook> = emptyList(),
    val errorMessage: String? = null
)
