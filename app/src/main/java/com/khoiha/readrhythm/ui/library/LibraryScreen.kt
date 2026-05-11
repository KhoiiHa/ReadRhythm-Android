package com.khoiha.readrhythm.ui.library

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun LibraryScreen() {
    ReadRhythmEmptyState(
        iconText = "R",
        title = "Your reading shelf is empty",
        message = "Add books and audiobooks here later to keep your current rhythm in one calm place."
    )
}
