package com.firebaseapp.controlefinanceiro.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Date

/**
 * Entity data class represents a single row in the database.
 */

@Entity(
    tableName = "word_exception",
    indices = [
        Index(
            value = ["wordId", "occurrenceIndex"],
            unique = true
        )
    ]
)
data class WordException(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val wordId: Int,
    val occurrenceIndex: Long,
    val date: LocalDateTime,
    val type: WordType,
    val value: BigDecimal,
    val categoryId: Int,
    val categoryName: String,
    val categoryIcon: String,
    val notes: String,
    val isDeleted: Boolean,
    val recurringGroup: String,
)