package com.khoiha.readrhythm.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.R
import com.khoiha.readrhythm.data.WeeklyActivityDay
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
                title = stringResource(R.string.insights_error_title),
                message = uiState.errorMessage
            )
        }
        uiState.isEmpty -> {
            ReadRhythmEmptyState(
                iconText = "I",
                title = stringResource(R.string.insights_empty_title),
                message = stringResource(R.string.insights_empty_message)
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
            text = stringResource(R.string.insights_loading),
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
        InsightCardData(
            stringResource(R.string.insights_total_minutes),
            uiState.totalMinutes.toString(),
            stringResource(R.string.insights_total_minutes_description)
        ),
        InsightCardData(
            stringResource(R.string.insights_total_sessions),
            uiState.totalSessions.toString(),
            stringResource(R.string.insights_total_sessions_description)
        ),
        InsightCardData(
            stringResource(R.string.insights_active_titles),
            uiState.activeTitles.toString(),
            stringResource(R.string.insights_active_titles_description)
        ),
        InsightCardData(
            stringResource(R.string.insights_completed_titles),
            uiState.completedTitles.toString(),
            stringResource(R.string.insights_completed_titles_description)
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
            WeeklyActivitySection(days = uiState.weeklyActivity)
        }

        items(cards) { card ->
            InsightCard(card = card)
        }
    }
}

@Composable
private fun WeeklyActivitySection(
    days: List<WeeklyActivityDay>
) {
    val maxMinutes = days.maxOfOrNull { it.minutes } ?: 0
    val weeklyMinutes = days.sumOf { it.minutes }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.insights_weekly_activity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.insights_weekly_activity_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Text(
                    text = weeklySummaryText(weeklyMinutes),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    WeeklyActivityDayColumn(
                        day = day,
                        maxMinutes = maxMinutes
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyActivityDayColumn(
    day: WeeklyActivityDay,
    maxMinutes: Int
) {
    val barHeight = if (maxMinutes > 0) {
        (18 + (day.minutes.toFloat() / maxMinutes) * 54).dp
    } else {
        18.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Surface(
            modifier = Modifier
                .width(24.dp)
                .height(barHeight),
            shape = MaterialTheme.shapes.small,
            color = if (day.minutes > 0) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.74f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {}

        Text(
            text = day.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${day.minutes}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
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

@Composable
private fun weeklySummaryText(minutes: Int): String {
    return when (minutes) {
        0 -> stringResource(R.string.insights_weekly_no_minutes)
        1 -> stringResource(R.string.insights_weekly_one_minute)
        else -> stringResource(R.string.insights_weekly_minutes, minutes)
    }
}
