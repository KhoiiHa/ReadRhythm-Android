package com.khoiha.readrhythm.data

import com.khoiha.readrhythm.data.local.BookEntity
import com.khoiha.readrhythm.data.local.ReadingDao
import com.khoiha.readrhythm.data.local.ReadingFormat
import com.khoiha.readrhythm.data.local.ReadingSessionEntity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            readingDao.observeCompletedTitles(),
            observeWeeklyActivity()
        ) { totalMinutes, totalSessions, activeTitles, completedTitles, weeklyActivity ->
            InsightsSummary(
                totalMinutes = totalMinutes,
                totalSessions = totalSessions,
                activeTitles = activeTitles,
                completedTitles = completedTitles,
                weeklyActivity = weeklyActivity
            )
        }
    }

    private fun observeWeeklyActivity(): Flow<List<WeeklyActivityDay>> {
        val days = lastSevenDays()
        val startTime = days.first().startTime

        return readingDao.observeSessionsSince(startTime)
            .map { sessions ->
                days.map { day ->
                    val minutes = sessions
                        .filter { session ->
                            session.createdAt >= day.startTime && session.createdAt < day.endTime
                        }
                        .sumOf { it.minutes }

                    WeeklyActivityDay(
                        label = day.label,
                        minutes = minutes
                    )
                }
            }
    }

    suspend fun insertBook(book: BookEntity): Long {
        return readingDao.insertBook(book)
    }

    suspend fun addDiscoverBook(book: DiscoverBook): AddDiscoverBookResult {
        val existingBySource = readingDao.getBookBySourceId(book.sourceId)
        if (existingBySource != null) {
            return AddDiscoverBookResult.AlreadyExists
        }

        val fallbackAuthor = book.firstAuthor.orEmpty()
        val existingByTitleAndAuthor = readingDao.getBookByTitleAndAuthor(
            title = book.title.trim(),
            author = fallbackAuthor
        )
        if (existingByTitleAndAuthor != null) {
            return AddDiscoverBookResult.AlreadyExists
        }

        readingDao.insertBook(
            BookEntity(
                sourceId = book.sourceId,
                thumbnailUrl = book.thumbnailUrl,
                title = book.title.trim(),
                author = book.firstAuthor,
                format = ReadingFormat.BOOK,
                progress = 0,
                totalUnits = book.pageCount ?: 0,
                createdAt = System.currentTimeMillis()
            )
        )
        return AddDiscoverBookResult.Saved
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
    val completedTitles: Int,
    val weeklyActivity: List<WeeklyActivityDay>
)

data class WeeklyActivityDay(
    val label: String,
    val minutes: Int
)

enum class AddDiscoverBookResult {
    Saved,
    AlreadyExists
}

private data class ActivityDayWindow(
    val label: String,
    val startTime: Long,
    val endTime: Long
)

private fun lastSevenDays(): List<ActivityDayWindow> {
    val labelFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val today = Calendar.getInstance().startOfDay()

    return (6 downTo 0).map { daysAgo ->
        val start = today.copy().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }
        val end = start.copy().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }

        ActivityDayWindow(
            label = labelFormat.format(start.time),
            startTime = start.timeInMillis,
            endTime = end.timeInMillis
        )
    }
}

private fun Calendar.startOfDay(): Calendar {
    return apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private fun Calendar.copy(): Calendar {
    return clone() as Calendar
}
