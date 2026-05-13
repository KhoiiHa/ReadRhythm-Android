package com.khoiha.readrhythm.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun InsightsScreen(
    uiState: InsightsUiState
) {
    when {
        uiState.isLoading -> InsightsLoadingState()
        uiState.errorMessage != null -> {
            ReadRhythmEmptyState(
                iconText = "!",
                title = "Insights could not load",
                message = uiState.errorMessage
            )
        }
        uiState.isEmpty -> {
            ReadRhythmEmptyState(
                iconText = "I",
                title = "Insights need a rhythm",
                message = "Save a few sessions and this space will summarize your reading and listening pace."
            )
        }
        else -> InsightsContent(uiState = uiState)
    }
}

@Composable
private fun InsightsLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading insights...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InsightsContent(
    uiState: InsightsUiState
) {
    val cards = listOf(
        InsightCardData("Total Minutes", uiState.totalMinutes.toString(), "Time spent reading and listening"),
        InsightCardData("Total Sessions", uiState.totalSessions.toString(), "Saved sessions"),
        InsightCardData("Active Titles", uiState.activeTitles.toString(), "Still in progress"),
        InsightCardData("Completed Titles", uiState.completedTitles.toString(), "Finished titles")
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(cards) { card ->
            InsightCard(card = card)
        }
    }
}

@Composable
private fun InsightCard(
    card: InsightCardData
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 148.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = card.value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = card.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = card.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class InsightCardData(
    val label: String,
    val value: String,
    val description: String
)
