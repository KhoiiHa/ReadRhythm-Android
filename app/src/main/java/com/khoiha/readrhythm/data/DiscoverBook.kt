package com.khoiha.readrhythm.data

data class DiscoverBook(
    val id: String,
    val sourceId: String,
    val title: String,
    val authors: String?,
    val firstAuthor: String?,
    val pageCount: Int?,
    val description: String?
)
