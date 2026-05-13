package com.khoiha.readrhythm.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("printType") printType: String = "books",
        @Query("key") apiKey: String? = null
    ): GoogleBooksResponseDto
}
