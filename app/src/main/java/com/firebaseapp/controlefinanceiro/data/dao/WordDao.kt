package com.firebaseapp.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordException
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.Date

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertException(word: WordException)

    @Update
    suspend fun update(word: Word)

    @Update
    suspend fun updateException(exception: WordException)

    @Delete
    suspend fun delete(word: Word)

    @Delete
    suspend fun deleteException(exception: WordException)

    @Query("SELECT * from words WHERE id = :id")
    fun getWord(id: Int): Flow<Word?>

    @Query("SELECT * from words WHERE id = :id")
    suspend fun getWordAsync(id: Int): Word?

    @Query("SELECT * from words ORDER BY date ASC")
    fun getAllWords() : Flow<List<Word>>

    @Query("SELECT * from word_exception ORDER BY date ASC")
    fun getAllWordsExceptions() : Flow<List<WordException>>

    @Query("SELECT * from words ORDER BY date ASC")
    suspend fun getAllWordsAsync() : List<Word>

    @Query("SELECT * from word_exception WHERE wordId = :id ORDER BY date ASC")
    suspend fun getWordExceptionsByWordAsync(id: Int) : List<WordException>

    @Query("SELECT * from word_exception ORDER BY date ASC")
    suspend fun getAllWordsExceptionsAsync() : List<WordException>

    @Query("SELECT * from words WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getWordsByDate(from: LocalDateTime, to: LocalDateTime) : Flow<List<Word>>

    @Query("SELECT * FROM word_exception WHERE wordId = :wordId AND occurrenceIndex = :occurrenceIndex LIMIT 1")
    fun getException(wordId: Int, occurrenceIndex: Long): Flow<WordException?>

    @Query("SELECT * from words WHERE categoryId = :id")
    fun wordsByCategory(id: Int): List<Word>

    @Query("SELECT * from word_exception WHERE categoryId = :id")
    fun exceptionsByCategory(id: Int): List<WordException>

    @Query("SELECT * from words WHERE recurringGroup = :recurringGroup")
    fun wordsByRecurringGroup(recurringGroup: String): List<Word>

    @Query("SELECT * from word_exception WHERE recurringGroup = :recurringGroup")
    fun exceptionsByRecurringGroup(recurringGroup: String): List<WordException>

}