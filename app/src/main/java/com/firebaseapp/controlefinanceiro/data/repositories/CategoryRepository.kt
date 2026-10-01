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

import com.firebaseapp.controlefinanceiro.data.dao.CategoryDao
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
     fun getAllCategoryStream(query: String): Flow<List<Category>> = categoryDao.getAllCategories(query)

     fun getCategoriesByTypeStream(type: CategoryType): Flow<List<Category>> = categoryDao.getCategoriesByType(type)

     fun getCategoryStream(id: Int): Flow<Category?> = categoryDao.getCategory(id)

     suspend fun insertCategory(category: Category) = categoryDao.insert(category)

     suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

     suspend fun updateCategory(category: Category) = categoryDao.update(category)

}
