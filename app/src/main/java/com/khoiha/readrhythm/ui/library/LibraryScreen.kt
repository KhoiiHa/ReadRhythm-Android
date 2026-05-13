package com.khoiha.readrhythm.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onAddBook: (title: String, author: String?, format: ReadingFormat, totalUnits: Int) -> Unit,
    onDeleteBook: (BookEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit
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
                    title = "Start your reading shelf",
                    message = "Add a book or audiobook to begin tracking quiet progress over time."
                )
            }
            else -> {
                LibraryContentState(
                    books = uiState.books,
                    onDeleteBook = onDeleteBook,
                    onBookClick = onBookClick
                )
            }
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
        message = "$message Try again after reopening the app."
    )
}

@Composable
private fun LibraryContentState(
    books: List<BookEntity>,
    onDeleteBook: (BookEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = books,
            key = { book -> book.id }
        ) { book ->
            LibraryBookCard(
                book = book,
                onDeleteBook = onDeleteBook,
                onBookClick = onBookClick
            )
        }
    }
}
