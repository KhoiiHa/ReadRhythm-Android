package com.khoiha.readrhythm.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): GoogleBooksResponseDto
}
