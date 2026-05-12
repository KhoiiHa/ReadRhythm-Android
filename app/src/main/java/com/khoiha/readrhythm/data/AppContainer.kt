package com.khoiha.readrhythm.data

import android.content.Context
import androidx.room.Room
import com.khoiha.readrhythm.data.local.AppDatabase

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "readrhythm.db"
    ).build()

    val readingRepository = ReadingRepository(database.readingDao())
}
