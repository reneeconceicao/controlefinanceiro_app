package com.firebaseapp.controlefinanceiro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date,
    val hours: Double,
    val minutes: Double,
    val notes: String
)