package com.khoiha.readrhythm.data

import com.khoiha.readrhythm.data.remote.GoogleBooksApiService
import com.khoiha.readrhythm.data.remote.GoogleBookVolumeDto

class DiscoverRepository(
    private val googleBooksApiService: GoogleBooksApiService
) {
    suspend fun searchBooks(query: String): List<DiscoverBook> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            return emptyList()
        }

        return googleBooksApiService.searchBooks(query = trimmedQuery)
            .items
            .orEmpty()
            .mapNotNull { it.toDiscoverBook() }
    }
}

private fun GoogleBookVolumeDto.toDiscoverBook(): DiscoverBook? {
    val info = volumeInfo ?: return null
    val title = info.title?.takeIf { it.isNotBlank() } ?: return null

    return DiscoverBook(
        id = id,
        title = title,
        authors = info.authors
            ?.filter { it.isNotBlank() }
            ?.joinToString(", ")
            ?.takeIf { it.isNotBlank() },
        pageCount = info.pageCount,
        description = info.subtitle?.takeIf { it.isNotBlank() }
            ?: info.description?.takeIf { it.isNotBlank() }
    )
}
