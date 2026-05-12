package com.khoiha.readrhythm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun observeBook(bookId: Long): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBook(bookId: Long): BookEntity?

    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun observeSessionsForBook(bookId: Long): Flow<List<ReadingSessionEntity>>

    @Query("SELECT COALESCE(SUM(minutes), 0) FROM reading_sessions")
    fun observeTotalMinutes(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reading_sessions")
    fun observeTotalSessions(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE totalUnits = 0 OR progress < totalUnits")
    fun observeActiveTitles(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE totalUnits > 0 AND progress >= totalUnits")
    fun observeCompletedTitles(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: ReadingSessionEntity): Long

    @Delete
    suspend fun deleteSession(session: ReadingSessionEntity)

    @Query("UPDATE books SET progress = :progress WHERE id = :bookId")
    suspend fun updateBookProgress(bookId: Long, progress: Int)

    @Transaction
    suspend fun insertSessionAndUpdateProgress(session: ReadingSessionEntity): Long {
        val sessionId = insertSession(session)
        val book = getBook(session.bookId)

        if (book != null && session.progressAmount > 0) {
            val nextProgress = calculateNextProgress(
                currentProgress = book.progress,
                progressAmount = session.progressAmount,
                totalUnits = book.totalUnits
            )
            updateBookProgress(book.id, nextProgress)
        }

        return sessionId
    }

    private fun calculateNextProgress(
        currentProgress: Int,
        progressAmount: Int,
        totalUnits: Int
    ): Int {
        val nextProgress = (currentProgress + progressAmount).coerceAtLeast(0)
        return if (totalUnits > 0) {
            nextProgress.coerceAtMost(totalUnits)
        } else {
            nextProgress
        }
    }
}
