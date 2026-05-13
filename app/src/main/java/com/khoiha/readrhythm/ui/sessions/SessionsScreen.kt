package com.khoiha.readrhythm.ui.sessions

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun SessionsScreen() {
    ReadRhythmEmptyState(
        iconText = "S",
        title = "Sessions collect in each title",
        message = "Open a book or audiobook to add focused reading and listening time."
    )
}
