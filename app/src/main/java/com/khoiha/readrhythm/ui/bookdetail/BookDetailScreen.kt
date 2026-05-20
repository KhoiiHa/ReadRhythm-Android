package com.khoiha.readrhythm.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.khoiha.readrhythm.R
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
                    Text(text = uiState.book?.title ?: stringResource(R.string.book_detail_default_title))
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
                        title = stringResource(R.string.book_detail_error_title),
                        message = uiState.errorMessage
                            ?: stringResource(R.string.book_detail_error_message)
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
            text = stringResource(R.string.book_detail_loading),
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
        stringResource(
            R.string.library_progress_with_total,
            book.progress.coerceAtLeast(0),
            book.totalUnits,
            unitLabel(book)
        )
    } else {
        stringResource(
            R.string.library_progress_without_total,
            book.progress.coerceAtLeast(0),
            unitLabel(book)
        )
    }
    val percentText = if (hasProgressTarget) {
        stringResource(R.string.library_percent_complete, (progress * 100).toInt())
    } else {
        stringResource(R.string.library_no_target_set)
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

        BookHeroSection(
            book = book,
            isCompleted = isCompleted
        )

        ProgressSection(
            hasProgressTarget = hasProgressTarget,
            progress = progress,
            progressText = progressText,
            percentText = percentText
        )

        BookInfoSection(
            book = book,
            sessionCount = sessions.size
        )

        Button(
            onClick = { showAddSessionDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSavingSession
        ) {
            Text(
                if (isSavingSession) {
                    stringResource(R.string.book_detail_saving_session)
                } else {
                    stringResource(R.string.book_detail_add_session)
                }
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            SessionsSection(
                sessions = sessions,
                book = book
            )
        }
    }

    if (showAddSessionDialog) {
        AddSessionDialog(
            format = book.format,
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
private fun BookHeroSection(
    book: BookEntity,
    isCompleted: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            DetailBookCover(
                title = book.title,
                thumbnailUrl = book.thumbnailUrl
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatLabel(book),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author ?: stringResource(R.string.common_unknown_author),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isCompleted) {
                    CompletedBadge()
                }
            }
        }
    }
}

@Composable
private fun DetailBookCover(
    title: String,
    thumbnailUrl: String?
) {
    Surface(
        modifier = Modifier
            .width(112.dp)
            .height(168.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = stringResource(R.string.common_cover_for, title),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(1).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProgressSection(
    hasProgressTarget: Boolean,
    progress: Float,
    progressText: String,
    percentText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.book_detail_progress_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = percentText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = progressText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hasProgressTarget) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.book_detail_no_target_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BookInfoSection(
    book: BookEntity,
    sessionCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.book_detail_details_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            DetailInfoRow(label = stringResource(R.string.book_detail_format_label), value = formatLabel(book))
            DetailInfoRow(label = stringResource(R.string.book_detail_activity_label), value = sessionCountLabel(sessionCount))
            DetailInfoRow(label = stringResource(R.string.book_detail_created_label), value = formatDate(book.createdAt))
        }
    }
}

@Composable
private fun CompletedBadge() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = stringResource(R.string.common_completed),
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
            text = stringResource(R.string.book_detail_sessions_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        if (sessions.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.book_detail_sessions_empty_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.book_detail_sessions_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

@Composable
private fun formatLabel(book: BookEntity): String {
    return when (book.format) {
        ReadingFormat.BOOK -> stringResource(R.string.common_book)
        ReadingFormat.AUDIOBOOK -> stringResource(R.string.common_audiobook)
    }
}

@Composable
private fun unitLabel(book: BookEntity): String {
    return when (book.format) {
        ReadingFormat.BOOK -> stringResource(R.string.common_pages)
        ReadingFormat.AUDIOBOOK -> stringResource(R.string.common_min)
    }
}

@Composable
private fun sessionCountLabel(count: Int): String {
    return if (count == 1) {
        stringResource(R.string.book_detail_one_session_logged)
    } else {
        stringResource(R.string.book_detail_sessions_logged, count)
    }
}
