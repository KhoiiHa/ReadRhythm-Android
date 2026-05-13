package com.khoiha.readrhythm.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.DiscoverBook
import com.khoiha.readrhythm.ui.components.ReadRhythmEmptyState

@Composable
fun DiscoverScreen(
    uiState: DiscoverUiState,
    onSearch: (String) -> Unit,
    onAddToLibrary: (DiscoverBook) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchHeader(
            query = query,
            isLoading = uiState.isLoading,
            onQueryChange = { query = it },
            onSearch = { onSearch(query) }
        )

        if (uiState.feedbackMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = uiState.feedbackMessage,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        when {
            uiState.isLoading -> DiscoverLoadingState()
            uiState.errorMessage != null -> {
                ReadRhythmEmptyState(
                    iconText = "!",
                    title = "Search could not load",
                    message = uiState.errorMessage
                )
            }
            !uiState.hasSearched -> {
                ReadRhythmEmptyState(
                    iconText = "D",
                    title = "Search for your next title",
                    message = "Find books from Google Books and inspect the basics before adding anything later."
                )
            }
            uiState.results.isEmpty() -> {
                ReadRhythmEmptyState(
                    iconText = "D",
                    title = "No results found",
                    message = "Try a title, author, or a more specific search term."
                )
            }
            else -> DiscoverResults(
                results = uiState.results,
                onAddToLibrary = onAddToLibrary
            )
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
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
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Title or author") },
                    singleLine = true,
                    enabled = !isLoading
                )

                Button(
                    onClick = onSearch,
                    enabled = !isLoading && query.isNotBlank()
                ) {
                    Text("Search")
                }
            }
        }
    }
}

@Composable
private fun DiscoverLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "Searching Google Books...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DiscoverResults(
    results: List<DiscoverBook>,
    onAddToLibrary: (DiscoverBook) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = results,
            key = { book -> book.id }
        ) { book ->
            DiscoverBookCard(
                book = book,
                onAddToLibrary = onAddToLibrary
            )
        }
    }
}

@Composable
private fun DiscoverBookCard(
    book: DiscoverBook,
    onAddToLibrary: (DiscoverBook) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (book.authors != null) {
                Text(
                    text = book.authors,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (book.pageCount != null) {
                Text(
                    text = "${book.pageCount} pages",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (book.description != null) {
                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { onAddToLibrary(book) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Library")
            }
        }
    }
}
