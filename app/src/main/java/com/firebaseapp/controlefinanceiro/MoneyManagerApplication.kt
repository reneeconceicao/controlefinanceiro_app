package com.firebaseapp.controlefinanceiro

import android.app.Application
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.data.MoneyManagerDatabase

class MoneyManagerApplication: Application() {
    val wordRepository: WordRepository by lazy {
        WordRepository(MoneyManagerDatabase.getDatabase(this).wordDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(MoneyManagerDatabase.getDatabase(this).categoryDao())
    }
}