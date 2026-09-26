package com.firebaseapp.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firebaseapp.controlefinanceiro.data.entities.Word
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("SELECT * from words WHERE id = :id")
    fun getWord(id: Int): Flow<Word?>

    @Query("SELECT * from words ORDER BY date ASC")
    fun getAllWords() : Flow<List<Word>>

    @Query("SELECT * from words WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    fun getWordsByDate(from: Date, to: Date) : Flow<List<Word>>


}