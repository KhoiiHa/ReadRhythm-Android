package com.khoiha.readrhythm.data

data class DiscoverBook(
    val id: String,
    val title: String,
    val authors: String?,
    val pageCount: Int?,
    val description: String?
)
