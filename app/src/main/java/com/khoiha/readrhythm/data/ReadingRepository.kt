package com.khoiha.readrhythm.data

import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingDao
import kotlinx.coroutines.flow.Flow

class ReadingRepository(
    private val readingDao: ReadingDao
) {
    fun observeBooks(): Flow<List<BookEntity>> {
        return readingDao.observeBooks()
    }
}
