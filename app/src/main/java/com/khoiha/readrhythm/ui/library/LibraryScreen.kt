package com.khoiha.readrhythm.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onAddBook: (title: String, author: String?, format: ReadingFormat, totalUnits: Int) -> Unit
) {
    var showAddBookDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LibraryLoadingState()
            uiState.errorMessage != null && uiState.books.isEmpty() -> {
                LibraryErrorState(message = uiState.errorMessage)
            }
            uiState.books.isEmpty() -> {
                ReadRhythmEmptyState(
                    iconText = "R",
                    title = "Your reading shelf is empty",
                    message = "Add books and audiobooks here later to keep your current rhythm in one calm place."
                )
            }
            else -> LibraryContentState(books = uiState.books)
        }

        FloatingActionButton(
            onClick = { showAddBookDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

    if (showAddBookDialog) {
        AddBookDialog(
            isSaving = uiState.isSaving,
            onCancel = { showAddBookDialog = false },
            onSave = { title, author, format, totalUnits ->
                onAddBook(title, author, format, totalUnits)
                showAddBookDialog = false
            }
        )
    }
}

@Composable
private fun LibraryLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading your library...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LibraryErrorState(message: String) {
    ReadRhythmEmptyState(
        iconText = "!",
        title = "Library could not load",
        message = message
    )
}

@Composable
private fun LibraryContentState(
    books: List<BookEntity>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = books,
            key = { book -> book.id }
        ) { book ->
            LibraryBookRow(book = book)
        }
    }
}

@Composable
private fun LibraryBookRow(
    book: BookEntity
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = book.author ?: "Unknown author",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LinearProgressIndicator(
                progress = {
                    if (book.totalUnits == 0) {
                        0f
                    } else {
                        (book.progress.toFloat() / book.totalUnits).coerceIn(0f, 1f)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = book.format.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${book.progress}/${book.totalUnits}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddBookDialog(
    isSaving: Boolean,
    onCancel: () -> Unit,
    onSave: (title: String, author: String?, format: ReadingFormat, totalUnits: Int) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var selectedFormat by rememberSaveable { mutableStateOf(ReadingFormat.BOOK) }
    var totalUnitsText by rememberSaveable { mutableStateOf("") }

    val trimmedTitle = title.trim()
    val canSave = trimmedTitle.isNotEmpty() && !isSaving

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onCancel()
            }
        },
        title = {
            Text(text = "Add to library")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Author optional") },
                    singleLine = true,
                    enabled = !isSaving
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFormat == ReadingFormat.BOOK,
                        onClick = { selectedFormat = ReadingFormat.BOOK },
                        label = { Text("Book") },
                        enabled = !isSaving
                    )
                    FilterChip(
                        selected = selectedFormat == ReadingFormat.AUDIOBOOK,
                        onClick = { selectedFormat = ReadingFormat.AUDIOBOOK },
                        label = { Text("Audiobook") },
                        enabled = !isSaving
                    )
                }

                OutlinedTextField(
                    value = totalUnitsText,
                    onValueChange = { totalUnitsText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Total units optional") },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        trimmedTitle,
                        author,
                        selectedFormat,
                        totalUnitsText.toIntOrNull() ?: 0
                    )
                },
                enabled = canSave
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        }
    )
}
