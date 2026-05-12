package com.khoiha.readrhythm.data

import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingDao
import com.khoiha.readrhythm.data.local.ReadingSessionEntity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow

class ReadingRepository(
    private val readingDao: ReadingDao
) {
    fun observeBooks(): Flow<List<BookEntity>> {
        return readingDao.observeBooks()
    }

    fun observeBook(bookId: Long): Flow<BookEntity?> {
        return readingDao.observeBook(bookId)
    }

    fun observeSessionsForBook(bookId: Long): Flow<List<ReadingSessionEntity>> {
        return readingDao.observeSessionsForBook(bookId)
    }

    fun observeInsights(): Flow<InsightsSummary> {
        return combine(
            readingDao.observeTotalMinutes(),
            readingDao.observeTotalSessions(),
            readingDao.observeActiveTitles(),
            readingDao.observeCompletedTitles()
        ) { totalMinutes, totalSessions, activeTitles, completedTitles ->
            InsightsSummary(
                totalMinutes = totalMinutes,
                totalSessions = totalSessions,
                activeTitles = activeTitles,
                completedTitles = completedTitles
            )
        }
    }

    suspend fun insertBook(book: BookEntity): Long {
        return readingDao.insertBook(book)
    }

    suspend fun addSession(session: ReadingSessionEntity): Long {
        return readingDao.insertSessionAndUpdateProgress(session)
    }

    suspend fun deleteBook(book: BookEntity) {
        readingDao.deleteBook(book)
    }
}

data class InsightsSummary(
    val totalMinutes: Int,
    val totalSessions: Int,
    val activeTitles: Int,
    val completedTitles: Int
)
