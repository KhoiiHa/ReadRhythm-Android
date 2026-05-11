package com.khoiha.readrhythm.ui.insights

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun InsightsScreen() {
    ReadRhythmEmptyState(
        iconText = "I",
        title = "Insights are coming soon",
        message = "Your reading minutes, listening time and weekly rhythm will appear here after sessions exist."
    )
}
