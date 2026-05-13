package com.khoiha.readrhythm.data

import android.content.Context
import androidx.room.Room
import com.khoiha.readrhythm.BuildConfig
import com.khoiha.readrhythm.data.local.AppDatabase
import com.khoiha.readrhythm.data.remote.GoogleBooksApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "readrhythm.db"
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    private val googleBooksApiService = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApiService::class.java)

    val readingRepository = ReadingRepository(database.readingDao())
    val discoverRepository = DiscoverRepository(
        googleBooksApiService = googleBooksApiService,
        apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
    )
}
