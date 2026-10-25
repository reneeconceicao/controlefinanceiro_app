/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebaseapp.controlefinanceiro.data.repositories

import android.content.Context
import com.firebaseapp.controlefinanceiro.data.dao.WordDao
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordException
import com.firebaseapp.controlefinanceiro.helpers.PhotoFilesHandler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class WordRepository(private val context: Context, private val itemDao: WordDao) {
    fun getAllWordsStream(): Flow<List<Word>> = itemDao.getAllWords()

    fun getAllWordsExceptionsStream(): Flow<List<WordException>> = itemDao.getAllWordsExceptions()

    suspend fun getAllWordsAsync(): List<Word> = itemDao.getAllWordsAsync()

    suspend fun getAllWordsExceptionsAsync(): List<WordException> =
        itemDao.getAllWordsExceptionsAsync()

    suspend fun getWordExceptionsByWordAsync(id: Int): List<WordException> =
        itemDao.getWordExceptionsByWordAsync(id)

    fun getWordsByDateStream(from: LocalDateTime, to: LocalDateTime): Flow<List<Word>> =
        itemDao.getWordsByDate(from, to)

    fun getWordStream(id: Int): Flow<Word?> = itemDao.getWord(id)

    suspend fun getWordAsync(id: Int): Word? = itemDao.getWordAsync(id)

    fun getWordExceptionStream(id: Int, occurrenceIndex: Long): Flow<WordException?> =
        itemDao.getException(id, occurrenceIndex)

    suspend fun insertWord(word: Word) {
        PhotoFilesHandler(context).saveFiles(word)
        itemDao.insert(word)
    }

    suspend fun insertWordException(word: WordException) = itemDao.insertException(word)

    suspend fun deleteWord(word: Word) = itemDao.delete(word)

    suspend fun deleteException(wordException: WordException) = itemDao.deleteException(wordException)

    suspend fun updateWord(word: Word, originalImages: List<String> = emptyList()) {
        PhotoFilesHandler(context).saveFilesOnUpdate(word, originalImages)
        itemDao.update(word)
    }

    suspend fun updateException(exception: WordException) = itemDao.updateException(exception)
    fun wordsByCategory(id: Int) = itemDao.wordsByCategory(id)

    fun exceptionsByCategory(id: Int) = itemDao.exceptionsByCategory(id)

    fun wordsByRecurringGroup(recurringGroup: String) = itemDao.wordsByRecurringGroup(recurringGroup)
    fun exceptionsByRecurringGroup(recurringGroup: String) = itemDao.exceptionsByRecurringGroup(recurringGroup)


}

