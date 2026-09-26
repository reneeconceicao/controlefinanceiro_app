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

import com.firebaseapp.controlefinanceiro.data.dao.WordDao
import com.firebaseapp.controlefinanceiro.data.entities.Word
import kotlinx.coroutines.flow.Flow
import java.util.Date

class WordRepository(private val itemDao: WordDao) {
     fun getAllWordsStream(): Flow<List<Word>> = itemDao.getAllWords()

     fun getWordsByDateStream(from: Date, to: Date): Flow<List<Word>> = itemDao.getWordsByDate(from, to)

     fun getWordStream(id: Int): Flow<Word?> = itemDao.getWord(id)

     suspend fun insertWord(word: Word) = itemDao.insert(word)

     suspend fun deleteWord(word: Word) = itemDao.delete(word)

     suspend fun updateWord(word: Word) = itemDao.update(word)
}
