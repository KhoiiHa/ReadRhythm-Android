package com.khoiha.readrhythm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun observeSessionsForBook(bookId: Long): Flow<List<ReadingSessionEntity>>

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
}
