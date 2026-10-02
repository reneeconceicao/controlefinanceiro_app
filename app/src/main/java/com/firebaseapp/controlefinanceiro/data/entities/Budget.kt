package com.firebaseapp.controlefinanceiro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val budgetName: String,
    val value: BigDecimal,
    val currentValue: BigDecimal = BigDecimal.ZERO,
    val categoryId: Int,
    val position: Int,
)
