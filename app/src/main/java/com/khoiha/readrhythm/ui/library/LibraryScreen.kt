package com.khoiha.readrhythm.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onAddBook: (title: String, author: String?, format: ReadingFormat, totalUnits: Int) -> Unit,
    onDeleteBook: (BookEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFormatFilterChange: (LibraryFormatFilter) -> Unit
) {
    var showAddBookDialog by rememberSaveable { mutableStateOf(false) }
    var bookPendingDelete by rememberSaveable { mutableStateOf<BookEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LibraryLoadingState()
            uiState.errorMessage != null && uiState.books.isEmpty() -> {
                LibraryErrorState(message = uiState.errorMessage)
            }
            uiState.books.isEmpty() -> {
                ReadRhythmEmptyState(
                    iconText = "R",
                    title = "Build your reading shelf",
                    message = "Add your first book manually with the plus button, or use Discover to find one from Google Books."
                )
            }
            else -> {
                LibraryContentState(
                    books = uiState.filteredBooks,
                    totalBooks = uiState.books.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    onDeleteBook = { bookPendingDelete = it },
                    onBookClick = onBookClick,
                    onSearchQueryChange = onSearchQueryChange,
                    onFormatFilterChange = onFormatFilterChange
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
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

    if (bookPendingDelete != null) {
        DeleteBookConfirmationDialog(
            book = bookPendingDelete,
            onCancel = { bookPendingDelete = null },
            onConfirm = {
                bookPendingDelete?.let(onDeleteBook)
                bookPendingDelete = null
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
private fun DeleteBookConfirmationDialog(
    book: BookEntity?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    if (book == null) return

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Remove \"${book.title}\"?")
        },
        text = {
            Text(
                text = "This also removes its sessions."
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete book")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LibraryContentState(
    books: List<BookEntity>,
    totalBooks: Int,
    searchQuery: String,
    selectedFilter: LibraryFormatFilter,
    onDeleteBook: (BookEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFormatFilterChange: (LibraryFormatFilter) -> Unit
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
        item {
            LibrarySearchAndFilter(
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
                onSearchQueryChange = onSearchQueryChange,
                onFormatFilterChange = onFormatFilterChange
            )
        }

        if (books.isEmpty()) {
            item {
                ReadRhythmEmptyState(
                    iconText = "R",
                    title = "No matching titles",
                    message = "Try another title or author, or switch the format filter across your $totalBooks saved titles."
                )
            }
        }

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

@Composable
private fun LibrarySearchAndFilter(
    searchQuery: String,
    selectedFilter: LibraryFormatFilter,
    onSearchQueryChange: (String) -> Unit,
    onFormatFilterChange: (LibraryFormatFilter) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search library") },
                singleLine = true
            )

            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LibraryFilterChip(
                    label = "All",
                    selected = selectedFilter == LibraryFormatFilter.ALL,
                    onClick = { onFormatFilterChange(LibraryFormatFilter.ALL) }
                )
                LibraryFilterChip(
                    label = "Books",
                    selected = selectedFilter == LibraryFormatFilter.BOOKS,
                    onClick = { onFormatFilterChange(LibraryFormatFilter.BOOKS) }
                )
                LibraryFilterChip(
                    label = "Audiobooks",
                    selected = selectedFilter == LibraryFormatFilter.AUDIOBOOKS,
                    onClick = { onFormatFilterChange(LibraryFormatFilter.AUDIOBOOKS) }
                )
            }
        }
    }
}

@Composable
private fun LibraryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}
