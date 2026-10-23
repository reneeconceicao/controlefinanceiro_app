package com.firebaseapp.controlefinanceiro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDateTime,
    val type: WordType,
    val value: BigDecimal,
    val categoryId: Int,
    val categoryName: String,
    val categoryIcon: String,
    val notes: String,
    val images: List<String>,
    val isRecurring: Boolean,// = false,
    val endDate: LocalDateTime?,// = date.plusMonths(6),
    val frequency: String = "M", //Day, Month, Year
    val interval: Int = 1,
    val recurringGroup: String = ""// = UUID.randomUUID().toString(),
)

enum class WordType(val value: String) {
    Income("word_income"),
    Expense("word_expense")
}