package com.khoiha.readrhythm.ui.insights

data class InsightsUiState(
    val isLoading: Boolean = true,
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val activeTitles: Int = 0,
    val completedTitles: Int = 0,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = totalSessions == 0 && activeTitles == 0 && completedTitles == 0
}
