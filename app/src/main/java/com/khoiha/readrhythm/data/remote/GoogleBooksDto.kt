package com.khoiha.readrhythm.data.remote

data class GoogleBooksResponseDto(
    val items: List<GoogleBookVolumeDto>? = null
)

data class GoogleBookVolumeDto(
    val id: String,
    val volumeInfo: GoogleBookVolumeInfoDto? = null
)

data class GoogleBookVolumeInfoDto(
    val title: String? = null,
    val subtitle: String? = null,
    val authors: List<String>? = null,
    val pageCount: Int? = null,
    val description: String? = null
)
