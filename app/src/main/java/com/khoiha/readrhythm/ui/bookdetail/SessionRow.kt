package com.khoiha.readrhythm.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.data.local.ReadingSessionEntity
import java.text.DateFormat
import java.util.Date

@Composable
fun SessionRow(
    session: ReadingSessionEntity,
    book: BookEntity
) {
    val progressUnit = when (book.format) {
        ReadingFormat.BOOK -> stringResource(R.string.common_pages)
        ReadingFormat.AUDIOBOOK -> stringResource(R.string.session_audio_progress_unit)
    }
    val progressText = if (session.progressAmount > 0) {
        stringResource(R.string.session_progress_added, session.progressAmount, progressUnit)
    } else {
        stringResource(R.string.session_no_progress)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.session_minutes, session.minutes),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(session.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (session.progressAmount > 0) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ) {
                Text(
                    text = progressText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (session.progressAmount > 0) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
}
