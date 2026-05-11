package com.khoiha.readrhythm.ui.sessions

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun SessionsScreen() {
    ReadRhythmEmptyState(
        iconText = "S",
        title = "Sessions will live here",
        message = "This space is reserved for focused reading and listening sessions once tracking is added."
    )
}
