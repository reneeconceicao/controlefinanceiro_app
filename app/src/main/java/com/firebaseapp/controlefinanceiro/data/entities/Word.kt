package com.firebaseapp.controlefinanceiro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date,
    val type: WordType,
    val value: BigDecimal,
    val categoryId: Int,
    val categoryName: String,
    val notes: String
)

enum class WordType(val value: String) {
    Income("word_income"),
    Expense("word_expense")
}