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

import com.firebaseapp.controlefinanceiro.data.dao.BudgetDao
import com.firebaseapp.controlefinanceiro.data.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.util.Date

class BudgetRepository(private val budgetDao: BudgetDao) {
     fun getAllBudgetsStream(): Flow<List<Budget>> = budgetDao.getAllBudgets()

     fun getBudgetStream(id: Int): Flow<Budget?> = budgetDao.getBudget(id)

     suspend fun insertBudget(budget: Budget) = budgetDao.insert(budget)

     suspend fun deleteBudget(budget: Budget) = budgetDao.delete(budget)

     suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
}
