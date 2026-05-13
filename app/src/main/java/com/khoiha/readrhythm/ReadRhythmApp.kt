package com.khoiha.readrhythm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.khoiha.readrhythm.data.AppContainer
import com.khoiha.readrhythm.navigation.ReadRhythmNavHost
import com.khoiha.readrhythm.ui.theme.ReadRhythmTheme

@Composable
fun ReadRhythmApp() {
    val context = LocalContext.current
    val appContainer = remember {
        AppContainer(context.applicationContext)
    }

    ReadRhythmTheme {
        ReadRhythmNavHost(
            readingRepository = appContainer.readingRepository,
            discoverRepository = appContainer.discoverRepository
        )
    }
}
