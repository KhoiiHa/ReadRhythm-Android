package com.khoiha.readrhythm.ui.sessions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.khoiha.readrhythm.R
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun SessionsScreen() {
    ReadRhythmEmptyState(
        iconText = "S",
        title = stringResource(R.string.sessions_tab_empty_title),
        message = stringResource(R.string.sessions_tab_empty_message)
    )
}
