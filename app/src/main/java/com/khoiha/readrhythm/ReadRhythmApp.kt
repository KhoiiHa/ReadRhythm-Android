package com.khoiha.readrhythm

import androidx.compose.runtime.Composable
import com.khoiha.readrhythm.navigation.ReadRhythmNavHost
import com.khoiha.readrhythm.ui.theme.ReadRhythmTheme

@Composable
fun ReadRhythmApp() {
    ReadRhythmTheme {
        ReadRhythmNavHost()
    }
}
