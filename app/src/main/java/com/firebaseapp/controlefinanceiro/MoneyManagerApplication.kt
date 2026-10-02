package com.firebaseapp.controlefinanceiro

import android.app.Application
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.data.MoneyManagerDatabase
import com.firebaseapp.controlefinanceiro.data.repositories.BudgetRepository

class MoneyManagerApplication: Application() {
    val wordRepository: WordRepository by lazy {
        WordRepository(MoneyManagerDatabase.getDatabase(this).wordDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(MoneyManagerDatabase.getDatabase(this).categoryDao())
    }

    val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(MoneyManagerDatabase.getDatabase(this).budgetDao())
    }
}