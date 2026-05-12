package com.khoiha.readrhythm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String?,
    val format: ReadingFormat,
    val progress: Int,
    val totalUnits: Int,
    val createdAt: Long
)
