package com.khoiha.readrhythm.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.data.local.ReadingSessionEntity
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onBack: () -> Unit,
    onAddSession: (minutes: Int, progressAmount: Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.book?.title ?: "Book Detail")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            text = "‹",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                uiState.isLoading -> BookDetailLoadingState()
                uiState.book == null -> {
                    ReadRhythmEmptyState(
                        iconText = "!",
                        title = "Book could not load",
                        message = uiState.errorMessage ?: "This book is no longer available."
                    )
                }
                else -> BookDetailContent(
                    book = uiState.book,
                    sessions = uiState.sessions,
                    isSavingSession = uiState.isSavingSession,
                    errorMessage = uiState.errorMessage,
                    onAddSession = onAddSession
                )
            }
        }
    }
}

@Composable
private fun BookDetailLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading book...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BookDetailContent(
    book: BookEntity,
    sessions: List<ReadingSessionEntity>,
    isSavingSession: Boolean,
    errorMessage: String?,
    onAddSession: (minutes: Int, progressAmount: Int) -> Unit
) {
    var showAddSessionDialog by rememberSaveable { mutableStateOf(false) }
    val hasProgressTarget = book.totalUnits > 0
    val isCompleted = hasProgressTarget && book.progress >= book.totalUnits
    val progress = if (hasProgressTarget) {
        (book.progress.toFloat() / book.totalUnits).coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressText = if (hasProgressTarget) {
        "${book.progress.coerceAtLeast(0)} / ${book.totalUnits} ${unitLabel(book)}"
    } else {
        "${book.progress.coerceAtLeast(0)} ${unitLabel(book)} tracked"
    }
    val percentText = if (hasProgressTarget) {
        "${(progress * 100).toInt()}% complete"
    } else {
        "No target yet"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (errorMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = book.author ?: "Unknown author",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isCompleted) {
                    CompletedBadge()
                }

                DetailInfoRow(label = "Format", value = formatLabel(book))
                DetailInfoRow(label = "Progress", value = progressText)
                DetailInfoRow(label = "Completion", value = percentText)
                DetailInfoRow(label = "Created", value = formatDate(book.createdAt))

                if (hasProgressTarget) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = { showAddSessionDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSavingSession
        ) {
            Text(if (isSavingSession) "Saving session..." else "Add Session")
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            SessionsSection(
                sessions = sessions,
                book = book
            )
        }
    }

    if (showAddSessionDialog) {
        AddSessionDialog(
            isSaving = isSavingSession,
            onCancel = { showAddSessionDialog = false },
            onSave = { minutes, progressAmount ->
                onAddSession(minutes, progressAmount)
                showAddSessionDialog = false
            }
        )
    }
}

@Composable
private fun CompletedBadge() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = "Completed",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SessionsSection(
    sessions: List<ReadingSessionEntity>,
    book: BookEntity
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Sessions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        if (sessions.isEmpty()) {
            Text(
                text = "No sessions yet. Add your first reading or listening session.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            sessions.forEach { session ->
                SessionRow(
                    session = session,
                    book = book
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
}

private fun formatLabel(book: BookEntity): String {
    return when (book.format) {
        ReadingFormat.BOOK -> "Book"
        ReadingFormat.AUDIOBOOK -> "Audiobook"
    }
}

private fun unitLabel(book: BookEntity): String {
    return when (book.format) {
        ReadingFormat.BOOK -> "pages"
        ReadingFormat.AUDIOBOOK -> "min"
    }
}
