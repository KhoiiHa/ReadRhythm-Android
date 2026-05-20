package com.khoiha.readrhythm.ui.sessions

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun SessionsScreen() {
    ReadRhythmEmptyState(
        iconText = "S",
        title = "Sessions live inside each title",
        message = "Open a book or audiobook from your Library and add a session to track time, progress, and insights."
    )
}
